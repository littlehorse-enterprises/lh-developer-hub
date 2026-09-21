package io.littlehorse.docs.conditionals;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable userId = wf.declareStr("user-id").required();
        WfRunVariable message = wf.declareStr("message").required();
        WfRunVariable preferredContact = wf.declareStr("contact-method");

        preferredContact.assign(wf.execute("fetch-contact-method", userId));
        wf.doIfElse(
                preferredContact.isEqualTo("COMLINK"),
                ifHandler -> ifHandler.execute("send-comlink-message", userId, message),
                elseHandler -> elseHandler.execute("send-hologram", userId, message));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        ContactTasks tasks = new ContactTasks();
        List<LHTaskWorker> workers = List.of(
            new LHTaskWorker(tasks, "fetch-contact-method", config),
            new LHTaskWorker(tasks, "send-comlink-message", config),
            new LHTaskWorker(tasks, "send-hologram", config));
        Workflow workflow = Workflow.newWorkflow("send-message", Main::wfLogic);

        System.out.println("Run with: lhctl run send-message user-id anakin message \"Hello there\"");
        ExampleSupport.start(config, workers, workflow);
    }
}