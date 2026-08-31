package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public final class ShipmentRuntime implements AutoCloseable {

    private final LittleHorseBlockingStub client;
    private final List<LHTaskWorker> workers;

    public ShipmentRuntime(LHConfig config) {
        this.client = config.getBlockingStub();
        ShipmentTasks tasks = new ShipmentTasks();
        this.workers = List.of(
                new LHTaskWorker(tasks, ShipmentWorkflow.CREATE_LABEL_TASK, config),
                new LHTaskWorker(tasks, ShipmentWorkflow.DISPATCH_TASK, config));
    }

    public LittleHorseBlockingStub client() {
        return client;
    }

    public void registerAndStart() {
        workers.forEach(LHTaskWorker::registerTaskDef);
        ShipmentWorkflow.build().registerWfSpec(client);
        workers.forEach(LHTaskWorker::start);
    }

    @Override
    public void close() {
        workers.forEach(LHTaskWorker::close);
    }
}
