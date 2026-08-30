package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.time.Instant;
import java.util.List;

public class TimestampExample {

    private static final String WORKFLOW_NAME = "timestamps";
    private static final String PUBLISH_BOOK_TASK = "publish-book";
    private static final String GET_CURRENT_DATE_TASK = "get-current-date";
    private static final String PRINT_BOOK_DETAILS_TASK = "print-book-details";

    private static final WorkflowRetentionPolicy RETENTION_POLICY = WorkflowRetentionPolicy.newBuilder()
            .setSecondsAfterWfTermination(3600)
            .build();

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable publishDate =
                wf.declareTimestamp("publish-date").withDefault(Instant.parse("1997-06-26T12:12:12Z"));
        WfRunVariable bookName = wf.declareStr("book-name").withDefault("Harry Potter and the Philosopher's Stone");

        wf.sleepUntil(publishDate);
        NodeOutput publishedBook = wf.execute(PUBLISH_BOOK_TASK, bookName, publishDate);
        NodeOutput currentDate = wf.execute(GET_CURRENT_DATE_TASK);
        wf.execute(PRINT_BOOK_DETAILS_TASK, publishedBook, currentDate);
    }

    public static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        Worker tasks = new Worker();
        return List.of(
                new LHTaskWorker(tasks, GET_CURRENT_DATE_TASK, config),
                new LHTaskWorker(tasks, PUBLISH_BOOK_TASK, config),
                new LHTaskWorker(tasks, PRINT_BOOK_DETAILS_TASK, config));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        Workflow workflow = Workflow.newWorkflow(WORKFLOW_NAME, TimestampExample::wfLogic)
                .withRetentionPolicy(RETENTION_POLICY);
        List<LHTaskWorker> workers = getTaskWorkers(config);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        workers.forEach(LHTaskWorker::registerTaskDef);
        workflow.registerWfSpec(config);
        workers.forEach(LHTaskWorker::start);
    }
}
