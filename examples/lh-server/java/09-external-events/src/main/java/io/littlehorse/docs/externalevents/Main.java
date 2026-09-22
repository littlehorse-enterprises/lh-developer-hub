package io.littlehorse.docs.externalevents;

import java.util.List;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name");
        name.assign(wf.waitForEvent("name-posted").registeredAs(String.class));
        wf.execute("greet", name);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        GreetingTasks tasks = new GreetingTasks();
        LHTaskWorker worker = new LHTaskWorker(tasks, "greet", config);
        Workflow workflow = Workflow.newWorkflow("greet-event", Main::wfLogic);

        System.out.println("Run with: lhctl run greet-event");
        System.out.println("Then post with: lhctl postEvent <wf-run-id> name-posted 'Obi-Wan'");
        ExampleSupport.start(config, List.of(worker), workflow);
    }
}