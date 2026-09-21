package io.littlehorse.docs;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public final class ExampleSupport {

    private ExampleSupport() {}

    public static void start(LHConfig config, List<LHTaskWorker> workers, Workflow... workflows) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));
        workers.forEach(LHTaskWorker::registerTaskDef);
        for (Workflow workflow : workflows) {
            workflow.registerWfSpec(config);
        }
        workers.forEach(LHTaskWorker::start);
    }
}