package io.littlehorse.docs.interrupts;

import java.util.List;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable allUnderpants = wf.declareJsonArr("all-underpants");
        wf.execute("start-underpants-collection");
        wf.waitForEvent("done-collecting-underpants");
        wf.execute("profit", allUnderpants);

        wf.registerInterruptHandler("underpant-collected", handler -> {
            WfRunVariable interruptContent = handler.declareStr(WorkflowThread.HANDLER_INPUT_VAR);
            allUnderpants.assign(allUnderpants.add(interruptContent));
            handler.execute("collect-underpant", interruptContent);
        }).withEventType(String.class);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        UnderpantsTasks tasks = new UnderpantsTasks();
        List<LHTaskWorker> workers = List.of(
            new LHTaskWorker(tasks, "start-underpants-collection", config),
            new LHTaskWorker(tasks, "collect-underpant", config),
            new LHTaskWorker(tasks, "profit", config));
        Workflow workflow = Workflow.newWorkflow("collect-underpants", Main::wfLogic);

        config.getBlockingStub().putExternalEventDef(PutExternalEventDefRequest.newBuilder()
            .setName("done-collecting-underpants")
            .build());

        System.out.println("Run with: lhctl run collect-underpants --wfRunId my-wf all-underpants '[]'");
        System.out.println("Then post with: lhctl postEvent my-wf underpant-collected STR Stan");
        ExampleSupport.start(config, workers, workflow);
    }
}