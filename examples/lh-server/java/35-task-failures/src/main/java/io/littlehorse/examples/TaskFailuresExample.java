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
    public static final String OPERATION_TASK = "perform-operation";
    public static final String RECOVER_TECHNICAL_TASK = "recover-technical-error";
    public static final String RECOVER_BUSINESS_TASK = "recover-business-exception";
    public static final String FINISH_TASK = "finish-operation";

    public static final String SUCCESS_SCENARIO = "success";
    public static final String RETRYABLE_SCENARIO = "retryable-runtime";
    public static final String BUSINESS_SCENARIO = "business-exception";

    public static final String BUSINESS_EXCEPTION_NAME = "inventory-unavailable";
    public static final String BUSINESS_EXCEPTION_MESSAGE = "The requested inventory is unavailable";

    private static final WorkflowRetentionPolicy RETENTION_POLICY = WorkflowRetentionPolicy.newBuilder()
            .setSecondsAfterWfTermination(3600)
            .build();

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable scenario = wf.declareStr("scenario").required();
        NodeOutput operation = wf.execute(OPERATION_TASK, scenario).withRetries(1);

        wf.handleError(operation, handler -> handler.execute(RECOVER_TECHNICAL_TASK));
        wf.handleException(operation, BUSINESS_EXCEPTION_NAME, handler -> {
            WfRunVariable content = handler.declareStr(WorkflowThread.HANDLER_INPUT_VAR);
            handler.execute(
                    RECOVER_BUSINESS_TASK,
                    BUSINESS_EXCEPTION_NAME,
                    BUSINESS_EXCEPTION_MESSAGE,
                    content);
        });

        wf.execute(FINISH_TASK, scenario).withRetries(1);
    }

    public static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        TaskFailureTasks tasks = new TaskFailureTasks();
        return List.of(
                new LHTaskWorker(tasks, OPERATION_TASK, config),
                new LHTaskWorker(tasks, RECOVER_TECHNICAL_TASK, config),
                new LHTaskWorker(tasks, RECOVER_BUSINESS_TASK, config),
                new LHTaskWorker(tasks, FINISH_TASK, config));
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
