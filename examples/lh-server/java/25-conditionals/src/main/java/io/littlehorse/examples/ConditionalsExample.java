package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class ConditionalsExample {

    private static final String WORKFLOW_NAME = "conditionals";
    private static final String RECORD_TASK = "record-request";
    private static final String LARGE_TASK = "route-large";
    private static final String VALIDATE_LARGE_TASK = "validate-large-request";
    private static final String NOTIFY_LARGE_TASK = "notify-large-request";
    private static final String EXPEDITED_TASK = "route-expedited";
    private static final String STANDARD_TASK = "route-standard";
    private static final String FINISH_TASK = "finish-request";

    private static final WorkflowRetentionPolicy RETENTION_POLICY = WorkflowRetentionPolicy.newBuilder()
            .setSecondsAfterWfTermination(3600)
            .build();

    public static Workflow getWorkflow() {
        return Workflow.newWorkflow(WORKFLOW_NAME, wf -> {
                    WfRunVariable amount = wf.declareInt("amount").required();
                    WfRunVariable expedited = wf.declareBool("expedited").required();

                    wf.execute(RECORD_TASK, amount, expedited).withRetries(1);

                    wf.doIf(amount.isGreaterThan(100L), large -> {
                        large.execute(LARGE_TASK, amount);
                        large.execute(VALIDATE_LARGE_TASK, amount);
                        large.execute(NOTIFY_LARGE_TASK, amount);
                    })
                            .doElseIf(expedited.isEqualTo(true), quick -> quick.execute(EXPEDITED_TASK, amount))
                            .doElse(standard -> standard.execute(STANDARD_TASK, amount));

                    wf.execute(FINISH_TASK).withRetries(1);
                })
                .withRetentionPolicy(RETENTION_POLICY);
    }

    public static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        ConditionalTasks tasks = new ConditionalTasks();
        return List.of(
                new LHTaskWorker(tasks, RECORD_TASK, config),
                new LHTaskWorker(tasks, LARGE_TASK, config),
                new LHTaskWorker(tasks, VALIDATE_LARGE_TASK, config),
                new LHTaskWorker(tasks, NOTIFY_LARGE_TASK, config),
                new LHTaskWorker(tasks, EXPEDITED_TASK, config),
                new LHTaskWorker(tasks, STANDARD_TASK, config),
                new LHTaskWorker(tasks, FINISH_TASK, config));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        Workflow workflow = getWorkflow();
        List<LHTaskWorker> workers = getTaskWorkers(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        workers.forEach(LHTaskWorker::registerTaskDef);
        workflow.registerWfSpec(config);
        workers.forEach(LHTaskWorker::start);
    }
}
