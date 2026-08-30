package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.CorrelatedEventConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.common.proto.ReturnType;
import io.littlehorse.sdk.common.proto.TypeDefinition;
import io.littlehorse.sdk.common.proto.VariableType;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.ExternalEventNodeOutput;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.SpawnedChildWf;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;
import java.util.Map;

public final class ChildWorkflowExample {

    public static final String CONTACT_CUSTOMER_WF = "contact-customer";
    public static final String PAYMENT_FLOW_WF = "payment-flow";
    public static final String PROCESS_ORDER_WF = "process-order";
    public static final String CUSTOMER_RESPONDED_EVENT = "customer-responded";
    public static final String RESTOCKED_EVENT = "restocked";

    private static final String SEND_EMAIL_TASK = "send-customer-email";
    private static final String SEND_SMS_TASK = "send-customer-sms";
    private static final String AUTHORIZE_PAYMENT_TASK = "authorize-payment";
    private static final String RECORD_PAYMENT_ISSUE_TASK = "record-payment-issue";
    private static final String CONFIRM_PAYMENT_TASK = "confirm-payment";
    private static final String CHECK_INVENTORY_TASK = "check-inventory";
    private static final String SHIP_ITEM_TASK = "ship-item";
    private static final String CANCEL_ORDER_TASK = "cancel-order";

    private static final String EMAIL = "email";
    private static final String INVALID_PAYMENT_METHOD = "invalid";
    private static final String OUT_OF_STOCK = "out-of-stock";

    private static final WorkflowRetentionPolicy RETENTION_POLICY = WorkflowRetentionPolicy.newBuilder()
            .setSecondsAfterWfTermination(14 * 24 * 60 * 60L)
            .build();

    private ChildWorkflowExample() {}

    public static void contactCustomer(WorkflowThread wf) {
        WfRunVariable eventId = wf.declareStr("event-id").required();
        WfRunVariable customerId = wf.declareStr("customer-id").required();
        WfRunVariable message = wf.declareStr("message").required();
        WfRunVariable contactMethod = wf.declareStr("contact-method").required();
        WfRunVariable response = wf.declareBool("response");

        wf.doIf(contactMethod.isEqualTo(EMAIL), email -> {
                    email.execute(SEND_EMAIL_TASK, customerId, message);
                    response.assign(waitForCustomerResponse(email, eventId));
                })
                .doElse(sms -> {
                    sms.execute(SEND_SMS_TASK, customerId, message);
                    response.assign(waitForCustomerResponse(sms, eventId));
                });

        wf.complete(response);
    }

    public static void paymentFlow(WorkflowThread wf) {
        WfRunVariable eventId = wf.declareStr("event-id").required();
        WfRunVariable customerId = wf.declareStr("customer-id").required();
        WfRunVariable paymentMethod = wf.declareStr("payment-method").required();
        WfRunVariable contactMethod = wf.declareStr("contact-method").required();
        WfRunVariable amount = wf.declareInt("amount").required();

        WfRunVariable paymentSucceeded = wf.declareBool("payment-succeeded");
        NodeOutput authorization = wf.execute(AUTHORIZE_PAYMENT_TASK, paymentMethod, amount);
        paymentSucceeded.assign(authorization);

        wf.doIf(paymentSucceeded.isEqualTo(true), approved -> approved.execute(CONFIRM_PAYMENT_TASK, amount))
                .doElse(rejected -> {
                    SpawnedChildWf contact = rejected.runWf(
                            CONTACT_CUSTOMER_WF,
                            Map.of(
                                    "event-id", eventId,
                                    "customer-id", customerId,
                                    "contact-method", contactMethod,
                                    "message", "Your payment method was rejected. Please update it."));
                    WfRunVariable customerResponded = rejected.declareBool("customer-responded");
                    customerResponded.assign(rejected.waitForChildWf(contact));
                    rejected.execute(RECORD_PAYMENT_ISSUE_TASK, customerId, customerResponded);
                    paymentSucceeded.assign(false);
                });
        wf.complete(paymentSucceeded);
    }

    public static void processOrder(WorkflowThread wf) {
        WfRunVariable eventId = wf.declareStr("event-id").required();
        WfRunVariable orderId = wf.declareStr("order-id").required();
        WfRunVariable customerId = wf.declareStr("customer-id").required();
        WfRunVariable item = wf.declareStr("item").required();
        WfRunVariable contactMethod = wf.declareStr("contact-method").required();
        WfRunVariable paymentMethod = wf.declareStr("payment-method").required();
        WfRunVariable amount = wf.declareInt("amount").required();

        WfRunVariable inStock = wf.declareBool("in-stock");
        WfRunVariable customerWantsWait = wf.declareBool("customer-wants-wait");
        WfRunVariable itemRestocked = wf.declareBool("item-restocked");
        WfRunVariable paymentSucceeded = wf.declareBool("payment-succeeded");
        inStock.assign(wf.execute(CHECK_INVENTORY_TASK, item));
        wf.doIf(inStock.isEqualTo(true), available -> payAndShip(
                        available,
                        eventId,
                        orderId,
                        customerId,
                        item,
                        contactMethod,
                        paymentMethod,
                        amount,
                        paymentSucceeded))
                .doElse(unavailable -> {
                    SpawnedChildWf contact = unavailable.runWf(
                            CONTACT_CUSTOMER_WF,
                            Map.of(
                                    "event-id", eventId,
                                    "customer-id", customerId,
                                    "contact-method", contactMethod,
                                    "message", "Your order is delayed because an item is out of stock."));
                    customerWantsWait.assign(unavailable.waitForChildWf(contact));
                    unavailable.doIf(customerWantsWait.isEqualTo(true), waiting -> {
                                ExternalEventNodeOutput restocked = waiting.waitForEvent(RESTOCKED_EVENT)
                                        .withCorrelationId(orderId)
                                        .registeredAs(Boolean.class);
                                itemRestocked.assign(restocked);
                                waiting.doIf(itemRestocked.isEqualTo(true), restockedBranch -> payAndShip(
                                                restockedBranch,
                                                eventId,
                                                orderId,
                                                customerId,
                                                item,
                                                contactMethod,
                                                paymentMethod,
                                                amount,
                                                paymentSucceeded))
                                        .doElse(cancelled -> cancelled.execute(
                                                CANCEL_ORDER_TASK, orderId, "item was not restocked"));
                            })
                            .doElse(cancelled -> cancelled.execute(
                                    CANCEL_ORDER_TASK, orderId, "customer did not want to wait"));
                });
    }

    private static void payAndShip(
            WorkflowThread thread,
            WfRunVariable eventId,
            WfRunVariable orderId,
            WfRunVariable customerId,
            WfRunVariable item,
            WfRunVariable contactMethod,
            WfRunVariable paymentMethod,
            WfRunVariable amount,
            WfRunVariable paymentSucceeded) {
        SpawnedChildWf payment = thread.runWf(
                PAYMENT_FLOW_WF,
                Map.of(
                        "event-id", eventId,
                        "customer-id", customerId,
                        "payment-method", paymentMethod,
                        "contact-method", contactMethod,
                        "amount", amount));
        paymentSucceeded.assign(thread.waitForChildWf(payment));
        thread.doIf(paymentSucceeded.isEqualTo(true), paid -> paid.execute(SHIP_ITEM_TASK, orderId, item))
                .doElse(unpaid -> unpaid.execute(CANCEL_ORDER_TASK, orderId, "payment failed"));
    }

    private static ExternalEventNodeOutput waitForCustomerResponse(WorkflowThread wf, WfRunVariable eventId) {
        return wf.waitForEvent(CUSTOMER_RESPONDED_EVENT)
                .withCorrelationId(eventId)
                .registeredAs(Boolean.class);
    }

    public static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        ExampleTasks tasks = new ExampleTasks();
        return List.of(
                new LHTaskWorker(tasks, SEND_EMAIL_TASK, config),
                new LHTaskWorker(tasks, SEND_SMS_TASK, config),
                new LHTaskWorker(tasks, AUTHORIZE_PAYMENT_TASK, config),
                new LHTaskWorker(tasks, RECORD_PAYMENT_ISSUE_TASK, config),
                new LHTaskWorker(tasks, CONFIRM_PAYMENT_TASK, config),
                new LHTaskWorker(tasks, CHECK_INVENTORY_TASK, config),
                new LHTaskWorker(tasks, SHIP_ITEM_TASK, config),
                new LHTaskWorker(tasks, CANCEL_ORDER_TASK, config));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub client = config.getBlockingStub();
        List<LHTaskWorker> workers = getTaskWorkers(config);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        registerCustomerResponseEvent(client);
        registerRestockedEvent(client);
        workers.forEach(LHTaskWorker::registerTaskDef);
        Workflow child = Workflow.newWorkflow(CONTACT_CUSTOMER_WF, ChildWorkflowExample::contactCustomer)
                .withRetentionPolicy(RETENTION_POLICY);
        Workflow payment = Workflow.newWorkflow(PAYMENT_FLOW_WF, ChildWorkflowExample::paymentFlow)
                .withRetentionPolicy(RETENTION_POLICY);
        Workflow order = Workflow.newWorkflow(PROCESS_ORDER_WF, ChildWorkflowExample::processOrder)
                .withRetentionPolicy(RETENTION_POLICY);

        child.registerWfSpec(client);
        payment.registerWfSpec(client);
        order.registerWfSpec(client);
        workers.forEach(LHTaskWorker::start);

        System.out.println("Registered contact-customer, payment-flow, and process-order.");
        System.out.println("Start either parent with lhctl, then publish customer-responded to its event-id.");
    }

    private static void registerCustomerResponseEvent(LittleHorseBlockingStub client) {
        client.putExternalEventDef(PutExternalEventDefRequest.newBuilder()
                .setName(CUSTOMER_RESPONDED_EVENT)
                .setContentType(ReturnType.newBuilder()
                        .setReturnType(TypeDefinition.newBuilder().setPrimitiveType(VariableType.BOOL)))
                .setCorrelatedEventConfig(CorrelatedEventConfig.newBuilder()
                        .setDeleteAfterFirstCorrelation(true)
                        .build())
                .build());
    }

    private static void registerRestockedEvent(LittleHorseBlockingStub client) {
        client.putExternalEventDef(PutExternalEventDefRequest.newBuilder()
                .setName(RESTOCKED_EVENT)
                .setContentType(ReturnType.newBuilder()
                        .setReturnType(TypeDefinition.newBuilder().setPrimitiveType(VariableType.BOOL)))
                .setCorrelatedEventConfig(CorrelatedEventConfig.newBuilder()
                        .setDeleteAfterFirstCorrelation(true)
                        .build())
                .build());
    }

    public static final class ExampleTasks {

        @io.littlehorse.sdk.worker.LHTaskMethod(SEND_EMAIL_TASK)
        public void sendEmail(String customerId, String message) {
            System.out.println("Email to " + customerId + ": " + message);
        }

        @io.littlehorse.sdk.worker.LHTaskMethod(SEND_SMS_TASK)
        public void sendSms(String customerId, String message) {
            System.out.println("SMS to " + customerId + ": " + message);
        }

        @io.littlehorse.sdk.worker.LHTaskMethod(AUTHORIZE_PAYMENT_TASK)
        public boolean authorizePayment(String paymentMethod, Long amount) {
            boolean authorized = !INVALID_PAYMENT_METHOD.equals(paymentMethod);
            System.out.println("Payment authorization for amount=" + amount + ": " + authorized);
            return authorized;
        }

        @io.littlehorse.sdk.worker.LHTaskMethod(RECORD_PAYMENT_ISSUE_TASK)
        public void recordPaymentIssue(String customerId, boolean customerResponded) {
            System.out.println("Recorded payment issue for " + customerId
                    + "; customer responded=" + customerResponded);
        }

        @io.littlehorse.sdk.worker.LHTaskMethod(CONFIRM_PAYMENT_TASK)
        public void confirmPayment(Long amount) {
            System.out.println("Confirmed payment amount=" + amount);
        }

        @io.littlehorse.sdk.worker.LHTaskMethod(CHECK_INVENTORY_TASK)
        public boolean checkInventory(String item) {
            boolean available = !OUT_OF_STOCK.equals(item);
            System.out.println("Inventory check for " + item + ": " + available);
            return available;
        }

        @io.littlehorse.sdk.worker.LHTaskMethod(SHIP_ITEM_TASK)
        public void shipItem(String orderId, String item) {
            System.out.println("Shipped item " + item + " for order " + orderId);
        }

        @io.littlehorse.sdk.worker.LHTaskMethod(CANCEL_ORDER_TASK)
        public void cancelOrder(String orderId, String reason) {
            System.out.println("Cancelled order " + orderId + "; reason=" + reason);
        }
    }
}
