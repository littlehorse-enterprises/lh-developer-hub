package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.worker.LHTaskWorker;
import jakarta.annotation.PreDestroy;
import java.util.List;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class OrderRuntime implements ApplicationRunner {

    private final LittleHorseBlockingStub client;
    private final List<LHTaskWorker> workers;

    public OrderRuntime(LHConfig config, LittleHorseBlockingStub client) {
        this.client = client;
        OrderTasks tasks = new OrderTasks();
        this.workers = List.of(
                new LHTaskWorker(tasks, OrderWorkflow.PREPARE_ORDER_TASK, config),
                new LHTaskWorker(tasks, OrderWorkflow.COMPLETE_ORDER_TASK, config));
    }

    @Override
    public void run(ApplicationArguments args) {
        workers.forEach(LHTaskWorker::registerTaskDef);
        client.putExternalEventDef(PutExternalEventDefRequest.newBuilder()
                .setName(OrderWorkflow.PAYMENT_EVENT)
                .build());
        OrderWorkflow.build().registerWfSpec(client);
        workers.forEach(LHTaskWorker::start);
    }

    @PreDestroy
    void closeWorkers() {
        workers.forEach(LHTaskWorker::close);
    }
}
