package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public final class CheckpointTasksExample {

    private static final String WORKFLOW_NAME = "checkpoint-tasks";

    private CheckpointTasksExample() {}

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name").required();
        wf.execute("checkpointed-side-effect", name).withRetries(2);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        CheckpointTasksWorker executable = new CheckpointTasksWorker();
        LHTaskWorker worker = new LHTaskWorker(executable, "checkpointed-side-effect", config);
        Runtime.getRuntime().addShutdownHook(new Thread(worker::close));

        worker.registerTaskDef();
        Workflow workflow = Workflow.newWorkflow(WORKFLOW_NAME, CheckpointTasksExample::wfLogic);
        workflow.registerWfSpec(config.getBlockingStub());
        worker.start();
    }
}
