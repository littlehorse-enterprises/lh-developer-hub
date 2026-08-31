package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.ThreadRetentionPolicy;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.SpawnedThread;
import io.littlehorse.sdk.wfsdk.SpawnedThreads;
import io.littlehorse.sdk.wfsdk.WaitForThreadsNodeOutput;
import io.littlehorse.sdk.wfsdk.UserTaskOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import java.util.List;
import java.util.Map;

public final class ChildThreadsExample {

    private static final String WORKFLOW_NAME = "customer-onboarding";
    private static final String MANUAL_ERP_ENTRY_FORM = "manual-erp-entry-form";
    private static final String IT_GROUP = "it-support";

    private ChildThreadsExample() {}

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable customer = wf.declareStr("customer").required();
        WfRunVariable erpSystems = wf.declareArray("erps", String.class);
        erpSystems.assign(wf.execute("fetch-erp-systems"));

        SpawnedThreads foreachChildren = wf.spawnThreadForEach(
                    erpSystems,
                    "erp-onboarding",
                    child -> {
                        WfRunVariable erpSystem = child.declareStr(WorkflowThread.HANDLER_INPUT_VAR);
                        child.execute("record-customer-in-erp", customer, erpSystem).withRetries(2);
                    },
                    Map.of());

        WaitForThreadsNodeOutput foreachJoin = wf.waitForThreads(foreachChildren);
        foreachJoin.handleAnyFailureOnChild(handler -> {
            UserTaskOutput manualEntry = handler.assignUserTask(
                    MANUAL_ERP_ENTRY_FORM, null, IT_GROUP);
            manualEntry.withNotes(handler.format(
                    "Manually enter customer {0} into the failed ERP system.", customer));
        });

        SpawnedThread firstFixedChild = wf.spawnThread(
                    child -> child.execute("notify-account-team", customer),
                    "notify-account-team",
                    Map.of());
        SpawnedThread secondFixedChild = wf.spawnThread(
                    child -> child.execute("provision-customer-portal", customer),
                    "provision-customer-portal",
                    Map.of());
        WaitForThreadsNodeOutput fixedJoin =
                wf.waitForThreads(SpawnedThreads.of(firstFixedChild, secondFixedChild));
        fixedJoin.handleAnyFailureOnChild(handler -> handler.execute("record-onboarding-failure", customer));

        wf.complete(wf.execute("customer-onboarding-complete", customer));
    }

    private static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        ChildThreadsWorker executable = new ChildThreadsWorker();
        return List.of(
                new LHTaskWorker(executable, "fetch-erp-systems", config),
                new LHTaskWorker(executable, "record-customer-in-erp", config),
                new LHTaskWorker(executable, "notify-account-team", config),
                new LHTaskWorker(executable, "provision-customer-portal", config),
                new LHTaskWorker(executable, "record-onboarding-failure", config),
                new LHTaskWorker(executable, "customer-onboarding-complete", config));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        var client = config.getBlockingStub();
        List<LHTaskWorker> workers = getTaskWorkers(config);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        workers.forEach(LHTaskWorker::registerTaskDef);
        client.putUserTaskDef(new UserTaskSchema(new ManualErpEntryForm(), MANUAL_ERP_ENTRY_FORM).compile());
        Workflow workflow = Workflow.newWorkflow(WORKFLOW_NAME, ChildThreadsExample::wfLogic)
                .withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                        .setSecondsAfterWfTermination(14 * 24 * 60 * 60L)
                        .build())
                .withDefaultThreadRetentionPolicy(ThreadRetentionPolicy.newBuilder()
                        .setSecondsAfterThreadTermination(7 * 24 * 60 * 60L)
                        .build());
        workflow.registerWfSpec(config.getBlockingStub());
        workers.forEach(LHTaskWorker::start);
    }
}
