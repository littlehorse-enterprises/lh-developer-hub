package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class TimestampExample {

    private static final String WORKFLOW_NAME = "timestamps";
    private static final String FORMAT_TASK = "format-timestamp";
    private static final String PRINT_TASK = "print-timestamp";

    private static final WorkflowRetentionPolicy RETENTION_POLICY = WorkflowRetentionPolicy.newBuilder()
            .setSecondsAfterWfTermination(3600)
            .build();

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable eventTime = wf.declareTimestamp("event-time").required();

        NodeOutput formattedTime = wf.execute(FORMAT_TASK, eventTime).withRetries(2);
        wf.execute(PRINT_TASK, formattedTime);
    }

    public static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        TimestampTasks tasks = new TimestampTasks();
        return List.of(
                new LHTaskWorker(tasks, FORMAT_TASK, config),
                new LHTaskWorker(tasks, PRINT_TASK, config));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        Workflow workflow = Workflow.newWorkflow(WORKFLOW_NAME, TimestampExample::wfLogic)
                .withRetentionPolicy(RETENTION_POLICY);
        List<LHTaskWorker> workers = getTaskWorkers(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        workers.forEach(LHTaskWorker::registerTaskDef);
        workflow.registerWfSpec(config);
        workers.forEach(LHTaskWorker::start);
    }
}
