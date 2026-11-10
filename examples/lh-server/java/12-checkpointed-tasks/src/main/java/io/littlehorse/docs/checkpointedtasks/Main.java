package io.littlehorse.docs.checkpointedtasks;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name").required();
        wf.execute("checkpoint-greet-java", name).withRetries(2);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        CheckpointTaskWorker tasks = new CheckpointTaskWorker();
        LHTaskWorker worker = new LHTaskWorker(tasks, "checkpoint-greet-java", config);
        Workflow workflow = Workflow.newWorkflow("checkpoint-tasks", Main::wfLogic);

        Runtime.getRuntime().addShutdownHook(new Thread(worker::close));
        worker.registerTaskDef();
        workflow.registerWfSpec(config);
        worker.start();

        System.out.println("Run with: lhctl run checkpoint-tasks name obiwan");
    }
}