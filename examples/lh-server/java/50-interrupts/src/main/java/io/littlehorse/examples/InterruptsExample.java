package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.ThreadRetentionPolicy;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InterruptsExample {

    public static final String WF_SPEC_NAME = "interrupts-demo";
    public static final String PROGRESS_INTERRUPT = "interrupt-progress";
    public static final String CANCEL_INTERRUPT = "interrupt-cancel";

    private static final Logger log = LoggerFactory.getLogger(InterruptsExample.class);

    private InterruptsExample() {}

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable remaining = wf.declareInt("remaining-work").withDefault(3);
        WfRunVariable status = wf.declareStr("status").withDefault("RUNNING");

        wf.registerInterruptHandler(PROGRESS_INTERRUPT, handler -> {
                    WfRunVariable amount = handler.declareInt(WorkflowThread.HANDLER_INPUT_VAR);

                    // This assignment targets the variable declared by the parent thread.
                    remaining.assign(remaining.subtract(amount));
                    handler.execute("record-progress", amount);
                })
                .withEventType(Integer.class);

        wf.registerInterruptHandler(CANCEL_INTERRUPT, handler -> {
                    WfRunVariable reason = handler.declareStr(WorkflowThread.HANDLER_INPUT_VAR);

                    status.assign("CANCELLED");
                    remaining.assign(0);
                    handler.execute("record-cancellation", reason);
                })
                .withEventType(String.class);

        wf.waitForCondition(remaining.isEqualTo(0));
        wf.execute("report-status", status);
        wf.complete(status);
    }

    public static List<LHTaskWorker> createWorkers(LHConfig config) {
        InterruptTasks tasks = new InterruptTasks();
        List<LHTaskWorker> workers = List.of(
                new LHTaskWorker(tasks, "record-progress", config),
                new LHTaskWorker(tasks, "record-cancellation", config),
                new LHTaskWorker(tasks, "report-status", config));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Closing interrupt demo workers");
            workers.forEach(LHTaskWorker::close);
        }));
        return workers;
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        List<LHTaskWorker> workers = createWorkers(config);

        workers.forEach(LHTaskWorker::registerTaskDef);
        Workflow workflow = Workflow.newWorkflow(WF_SPEC_NAME, InterruptsExample::wfLogic)
                .withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                        .setSecondsAfterWfTermination(24 * 60 * 60L)
                        .build())
                .withDefaultThreadRetentionPolicy(ThreadRetentionPolicy.newBuilder()
                        .setSecondsAfterThreadTermination(60 * 60L)
                        .build());
        workflow.registerWfSpec(config);
        workers.forEach(LHTaskWorker::start);
    }
}
