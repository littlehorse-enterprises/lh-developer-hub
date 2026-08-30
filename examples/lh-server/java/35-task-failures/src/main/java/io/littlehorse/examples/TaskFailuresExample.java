package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class TaskFailuresExample {

    public static final String WORKFLOW_NAME = "task-failures";
    public static final String PAYMENT_TASK = "make-payment";
    public static final String RECOVER_TECHNICAL_TASK = "recover-payment-api-error";
    public static final String HANDLE_INSUFFICIENT_FUNDS_TASK = "handle-insufficient-funds";
    public static final String HANDLE_INVALID_CREDIT_CARD_TASK = "handle-invalid-credit-card";
    public static final String PROCESS_SHIPMENT_TASK = "process-shipment";

    public static final String CREDIT_CARD_INPUT = "credit-card";
    public static final String AMOUNT_INPUT = "amount";
    public static final String INSUFFICIENT_FUNDS_CARD = "4000000000009995";
    public static final String INVALID_CREDIT_CARD = "4000000000000002";

    public static final String PAYMENT_REJECTED_EXCEPTION = "payment-rejected";
    public static final String INSUFFICIENT_FUNDS_CONTENT = "insufficient-funds";
    public static final String INVALID_CREDIT_CARD_CONTENT = "invalid-credit-card";

    private static final WorkflowRetentionPolicy RETENTION_POLICY = WorkflowRetentionPolicy.newBuilder()
            .setSecondsAfterWfTermination(3600)
            .build();

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable creditCard = wf.declareStr(CREDIT_CARD_INPUT).required().masked();
        WfRunVariable amount = wf.declareInt(AMOUNT_INPUT).required();
        NodeOutput payment = wf.execute(PAYMENT_TASK, creditCard, amount).withRetries(1);

        wf.handleError(payment, handler -> handler.execute(RECOVER_TECHNICAL_TASK));
        wf.handleException(payment, PAYMENT_REJECTED_EXCEPTION, handler -> {
            WfRunVariable content = handler.declareStr(WorkflowThread.HANDLER_INPUT_VAR);
            handler.doIf(content.isEqualTo(INSUFFICIENT_FUNDS_CONTENT), insufficientFunds ->
                            insufficientFunds.execute(HANDLE_INSUFFICIENT_FUNDS_TASK, content))
                    .doElse(content.isEqualTo(INVALID_CREDIT_CARD_CONTENT), invalidCreditCard ->
                            invalidCreditCard.execute(HANDLE_INVALID_CREDIT_CARD_TASK, content));
        });

        wf.execute(PROCESS_SHIPMENT_TASK, amount).withRetries(1);
    }

    public static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        TaskFailureTasks tasks = new TaskFailureTasks();
        return List.of(
                new LHTaskWorker(tasks, PAYMENT_TASK, config),
                new LHTaskWorker(tasks, RECOVER_TECHNICAL_TASK, config),
                new LHTaskWorker(tasks, HANDLE_INSUFFICIENT_FUNDS_TASK, config),
                new LHTaskWorker(tasks, HANDLE_INVALID_CREDIT_CARD_TASK, config),
                new LHTaskWorker(tasks, PROCESS_SHIPMENT_TASK, config));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        Workflow workflow = Workflow.newWorkflow(WORKFLOW_NAME, TaskFailuresExample::wfLogic)
                .withRetentionPolicy(RETENTION_POLICY);
        List<LHTaskWorker> workers = getTaskWorkers(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        workers.forEach(LHTaskWorker::registerTaskDef);
        workflow.registerWfSpec(config);
        workers.forEach(LHTaskWorker::start);
    }
}
