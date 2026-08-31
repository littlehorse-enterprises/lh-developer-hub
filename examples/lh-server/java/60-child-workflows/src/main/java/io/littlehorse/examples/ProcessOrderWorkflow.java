package io.littlehorse.examples;

import io.littlehorse.sdk.wfsdk.ExternalEventNodeOutput;
import io.littlehorse.sdk.wfsdk.SpawnedChildWf;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import java.util.Map;

public final class ProcessOrderWorkflow {

    public static final String NAME = "process-order";
    public static final String RESTOCKED_EVENT = "restocked";

    private static final String OUT_OF_STOCK = "out-of-stock";

    private ProcessOrderWorkflow() {}

    public static void define(WorkflowThread wf) {
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
        inStock.assign(wf.execute(ExampleTasks.CHECK_INVENTORY_TASK, item));

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
                            ContactCustomerWorkflow.NAME,
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
                                                ExampleTasks.CANCEL_ORDER_TASK,
                                                orderId,
                                                "item was not restocked"));
                            })
                            .doElse(cancelled -> cancelled.execute(
                                    ExampleTasks.CANCEL_ORDER_TASK,
                                    orderId,
                                    "customer did not want to wait"));
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
                PaymentFlowWorkflow.NAME,
                Map.of(
                        "event-id", eventId,
                        "customer-id", customerId,
                        "payment-method", paymentMethod,
                        "contact-method", contactMethod,
                        "amount", amount));
        paymentSucceeded.assign(thread.waitForChildWf(payment));
        thread.doIf(paymentSucceeded.isEqualTo(true), paid -> paid.execute(
                        ExampleTasks.SHIP_ITEM_TASK, orderId, item))
                .doElse(unpaid -> unpaid.execute(
                        ExampleTasks.CANCEL_ORDER_TASK, orderId, "payment failed"));
    }
}
