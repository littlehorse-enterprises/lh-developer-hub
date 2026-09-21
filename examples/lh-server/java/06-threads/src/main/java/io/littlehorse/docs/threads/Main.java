package io.littlehorse.docs.threads;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.SpawnedThread;
import io.littlehorse.sdk.wfsdk.SpawnedThreads;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;
import java.util.Map;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable parentVariable = wf.declareStr("parent-var")
                .withDefault("This is the parent variable's initial value");
        wf.execute("my-task", parentVariable);

        SpawnedThread child = wf.spawnThread(childThread -> {
            WfRunVariable childInput = childThread.declareStr("child-input").required();
            childThread.execute("my-task", childInput);
            childThread.execute("my-task", parentVariable);
            parentVariable.assign("This is the value of the parent variable set by the child.");
            childThread.sleepSeconds(45);
        }, "child", Map.of("child-input", "This is the input to the child thread"));

        wf.sleepSeconds(25);
        wf.execute("my-task", parentVariable);
        wf.waitForThreads(SpawnedThreads.of(child));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        ThreadTasks tasks = new ThreadTasks();
        LHTaskWorker worker = new LHTaskWorker(tasks, "my-task", config);
        Workflow workflow = Workflow.newWorkflow("threads-example", Main::wfLogic);

        System.out.println("Run with: lhctl run threads-example");
        ExampleSupport.start(config, List.of(worker), workflow);
    }
}