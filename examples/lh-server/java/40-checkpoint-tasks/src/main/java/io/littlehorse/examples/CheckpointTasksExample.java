package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.ThreadRetentionPolicy;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;

public final class CheckpointTasksExample {

    private static final String WORKFLOW_NAME = "checkpoint-tasks";

    private CheckpointTasksExample() {}

    public static Workflow getWorkflow() {
        Workflow workflow = Workflow.newWorkflow(WORKFLOW_NAME, wf -> {
            WfRunVariable name = wf.declareStr("name").required();
            NodeOutput result = wf.execute("checkpointed-side-effect", name).withRetries(2);
            wf.complete(result);
        });

        return workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                        .setSecondsAfterWfTermination(14 * 24 * 60 * 60L)
                        .build())
                .withDefaultThreadRetentionPolicy(ThreadRetentionPolicy.newBuilder()
                        .setSecondsAfterThreadTermination(7 * 24 * 60 * 60L)
                        .build());
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        CheckpointTasksWorker executable = new CheckpointTasksWorker();
        LHTaskWorker worker = new LHTaskWorker(executable, "checkpointed-side-effect", config);
        Runtime.getRuntime().addShutdownHook(new Thread(worker::close));

        worker.registerTaskDef();
        getWorkflow().registerWfSpec(config.getBlockingStub());
        worker.start();
    }
}
