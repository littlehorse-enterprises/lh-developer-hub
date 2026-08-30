package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LHErrorType;
import io.littlehorse.sdk.common.proto.ThreadRetentionPolicy;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.SpawnedThread;
import io.littlehorse.sdk.wfsdk.SpawnedThreads;
import io.littlehorse.sdk.wfsdk.WaitForThreadsNodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;
import java.util.Map;

public final class ChildThreadsExample {

    private static final String WORKFLOW_NAME = "child-threads";

    private ChildThreadsExample() {}

    public static Workflow getWorkflow() {
        Workflow workflow = Workflow.newWorkflow(WORKFLOW_NAME, wf -> {
            WfRunVariable items = wf.declareArray("items", String.class).required();

            SpawnedThreads foreachChildren = wf.spawnThreadForEach(
                    items,
                    "foreach-child",
                    child -> {
                        WfRunVariable item = child.declareStr(WorkflowThread.HANDLER_INPUT_VAR);
                        child.doIf(item.isEqualTo("decline"), declined -> declined.fail(
                                        "child-declined", "The child declined this item."))
                                .doElse(ready -> ready.execute("process-item", item).withRetries(1));
                    },
                    Map.of());

            WaitForThreadsNodeOutput foreachJoin = wf.waitForThreads(foreachChildren);
            foreachJoin.handleErrorOnChild(
                    LHErrorType.TASK_FAILURE,
                    handler -> handler.execute("record-technical-child-failure"));
            foreachJoin.handleExceptionOnChild(
                    "child-declined", handler -> handler.execute("record-declined-child"));

            SpawnedThread firstFixedChild = wf.spawnThread(
                    child -> child.execute("fixed-child", "fixed-child-one"),
                    "fixed-child-one",
                    Map.of());
            SpawnedThread secondFixedChild = wf.spawnThread(
                    child -> child.execute("fixed-child", "fixed-child-two"),
                    "fixed-child-two",
                    Map.of());
            WaitForThreadsNodeOutput fixedJoin =
                    wf.waitForThreads(SpawnedThreads.of(firstFixedChild, secondFixedChild));
            fixedJoin.handleAnyFailureOnChild(handler -> handler.execute("record-fixed-child-failure"));

            wf.complete(wf.execute("all-children-complete"));
        });

        return workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                        .setSecondsAfterWfTermination(14 * 24 * 60 * 60L)
                        .build())
                .withDefaultThreadRetentionPolicy(ThreadRetentionPolicy.newBuilder()
                        .setSecondsAfterThreadTermination(7 * 24 * 60 * 60L)
                        .build());
    }

    private static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        ChildThreadsWorker executable = new ChildThreadsWorker();
        return List.of(
                new LHTaskWorker(executable, "process-item", config),
                new LHTaskWorker(executable, "fixed-child", config),
                new LHTaskWorker(executable, "record-technical-child-failure", config),
                new LHTaskWorker(executable, "record-declined-child", config),
                new LHTaskWorker(executable, "record-fixed-child-failure", config),
                new LHTaskWorker(executable, "all-children-complete", config));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        List<LHTaskWorker> workers = getTaskWorkers(config);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        workers.forEach(LHTaskWorker::registerTaskDef);
        getWorkflow().registerWfSpec(config.getBlockingStub());
        workers.forEach(LHTaskWorker::start);
    }
}
