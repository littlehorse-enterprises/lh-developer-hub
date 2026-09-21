package io.littlehorse.docs.arrays;

import java.util.List;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.SpawnedThreads;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable numbers = wf.declareArray("my-array", Long.class).required();
        WfRunVariable valueToCheck = wf.declareInt("value-to-check").required();
        WfRunVariable arraySize = wf.declareInt("array-size");

        numbers.assign(wf.execute("produce-array"));
        numbers.assign(numbers.extend(4L));
        numbers.assign(numbers.removeIfPresent(2L));
        numbers.assign(numbers.removeIndex(1));
        arraySize.assign(numbers.size());
        wf.execute("process-item", numbers.get(0));
        wf.doIfElse(
                numbers.doesContain(valueToCheck),
                found -> found.execute("consume-array", numbers),
                notFound -> notFound.execute("process-item", valueToCheck));

        SpawnedThreads children = wf.spawnThreadForEach(numbers, "process-element", child -> {
            WfRunVariable input = child.declareInt(WorkflowThread.HANDLER_INPUT_VAR).required();
            child.execute("process-item", input);
        });
        wf.waitForThreads(children);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        ArrayTasks tasks = new ArrayTasks();
        List<LHTaskWorker> workers = List.of(
            new LHTaskWorker(tasks, "produce-array", config),
            new LHTaskWorker(tasks, "process-item", config),
            new LHTaskWorker(tasks, "consume-array", config));
        Workflow workflow = Workflow.newWorkflow("arrays-example", Main::wfLogic);

        System.out.println("Run with: lhctl run arrays-example my-array '[1,2,3]' value-to-check 3");
        ExampleSupport.start(config, workers, workflow);
    }
}