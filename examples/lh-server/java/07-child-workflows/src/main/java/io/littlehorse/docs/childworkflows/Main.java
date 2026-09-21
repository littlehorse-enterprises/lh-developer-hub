package io.littlehorse.docs.childworkflows;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.SpawnedChildWf;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;
import java.util.Map;

public class Main {

    public static void childLogic(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name").required();
        wf.complete(wf.execute("greet", name));
    }

    public static void parentLogic(WorkflowThread wf) {
        WfRunVariable inputName = wf.declareStr("input-name").required();
        WfRunVariable childOutput = wf.declareStr("child-output");
        SpawnedChildWf childWf = wf.runWf("greeting-child", Map.of("name", inputName));

        wf.execute("greet", "hi from parent");
        childOutput.assign(wf.waitForChildWf(childWf));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        GreetingTasks tasks = new GreetingTasks();
        LHTaskWorker worker = new LHTaskWorker(tasks, "greet", config);
        Workflow child = Workflow.newWorkflow("greeting-child", Main::childLogic);
        Workflow parent = Workflow.newWorkflow("greeting-parent", Main::parentLogic);

        System.out.println("Run with: lhctl run greeting-parent input-name \"Obi-Wan\"");
        ExampleSupport.start(config, List.of(worker), child, parent);
    }
}