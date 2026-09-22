package io.littlehorse.docs.maps;

import java.util.List;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable myMap = wf.declareMap("my-map", String.class, Long.class);
        NodeOutput produced = wf.execute("produce-map");
        myMap.assign(produced);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        MapTasks tasks = new MapTasks();
        LHTaskWorker worker = new LHTaskWorker(tasks, "produce-map", config);
        Workflow workflow = Workflow.newWorkflow("maps-example", Main::wfLogic);

        System.out.println("Run with: lhctl run maps-example");
        ExampleSupport.start(config, List.of(worker), workflow);
    }
}