package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.CorrelatedEventConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.common.proto.ReturnType;
import io.littlehorse.sdk.common.proto.TypeDefinition;
import io.littlehorse.sdk.common.proto.VariableType;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public final class ChildWorkflowExample {

    private static final WorkflowRetentionPolicy RETENTION_POLICY = WorkflowRetentionPolicy.newBuilder()
            .setSecondsAfterWfTermination(14 * 24 * 60 * 60L)
            .build();

    private ChildWorkflowExample() {}

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub client = config.getBlockingStub();
        List<LHTaskWorker> workers = getTaskWorkers(config);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        registerExternalEvent(client, ContactCustomerWorkflow.CUSTOMER_RESPONDED_EVENT);
        registerExternalEvent(client, ProcessOrderWorkflow.RESTOCKED_EVENT);
        workers.forEach(LHTaskWorker::registerTaskDef);

        Workflow contactCustomer = Workflow.newWorkflow(
                        ContactCustomerWorkflow.NAME, ContactCustomerWorkflow::define)
                .withRetentionPolicy(RETENTION_POLICY);
        Workflow paymentFlow = Workflow.newWorkflow(PaymentFlowWorkflow.NAME, PaymentFlowWorkflow::define)
                .withRetentionPolicy(RETENTION_POLICY);
        Workflow processOrder = Workflow.newWorkflow(ProcessOrderWorkflow.NAME, ProcessOrderWorkflow::define)
                .withRetentionPolicy(RETENTION_POLICY);

        // Child and payment specs must be registered before the order spec references them.
        contactCustomer.registerWfSpec(client);
        paymentFlow.registerWfSpec(client);
        processOrder.registerWfSpec(client);
        workers.forEach(LHTaskWorker::start);

        System.out.println("Registered contact-customer, payment-flow, and process-order.");
        System.out.println("Start either parent with lhctl, then publish customer-responded to its event-id.");
    }

    private static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        ExampleTasks tasks = new ExampleTasks();
        return List.of(
                new LHTaskWorker(tasks, ExampleTasks.SEND_EMAIL_TASK, config),
                new LHTaskWorker(tasks, ExampleTasks.SEND_SMS_TASK, config),
                new LHTaskWorker(tasks, ExampleTasks.AUTHORIZE_PAYMENT_TASK, config),
                new LHTaskWorker(tasks, ExampleTasks.RECORD_PAYMENT_ISSUE_TASK, config),
                new LHTaskWorker(tasks, ExampleTasks.CONFIRM_PAYMENT_TASK, config),
                new LHTaskWorker(tasks, ExampleTasks.CHECK_INVENTORY_TASK, config),
                new LHTaskWorker(tasks, ExampleTasks.SHIP_ITEM_TASK, config),
                new LHTaskWorker(tasks, ExampleTasks.CANCEL_ORDER_TASK, config));
    }

    private static void registerExternalEvent(LittleHorseBlockingStub client, String eventName) {
        client.putExternalEventDef(PutExternalEventDefRequest.newBuilder()
                .setName(eventName)
                .setContentType(ReturnType.newBuilder()
                        .setReturnType(TypeDefinition.newBuilder().setPrimitiveType(VariableType.BOOL)))
                .setCorrelatedEventConfig(CorrelatedEventConfig.newBuilder()
                        .setDeleteAfterFirstCorrelation(true)
                        .build())
                .build());
    }
}
