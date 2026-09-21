package io.littlehorse.docs.workflows;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name").searchable().required();
        wf.execute("greet", name);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        GreetingTasks tasks = new GreetingTasks();
        LHTaskWorker worker = new LHTaskWorker(tasks, "greet", config);
        Workflow workflow = Workflow.newWorkflow("quickstart", Main::wfLogic);

        System.out.println("Run with: lhctl run quickstart name Obi-Wan");
        ExampleSupport.start(config, List.of(worker), workflow);
    }
}