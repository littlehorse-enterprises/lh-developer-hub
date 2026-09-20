package io.littlehorse.quickstart;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutUserTaskDefRequest;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub client = config.getBlockingStub();
        OrderTasks ourTaskLogicObject = new OrderTasks();

        UserTaskSchema schema = new UserTaskSchema(new ApprovalForm(), "approve-it-rental");
        PutUserTaskDefRequest userTask = schema.compile();
        client.putUserTaskDef(userTask);

        LHTaskWorker shipItemWorker = new LHTaskWorker(ourTaskLogicObject, "ship-item", config);
        LHTaskWorker notApprovedWorker = new LHTaskWorker(ourTaskLogicObject, "decline-order", config);
        shipItemWorker.registerTaskDef();
        notApprovedWorker.registerTaskDef();

        new ITOrderWorkflow().getWorkflow().registerWfSpec(client);

        Runtime.getRuntime().addShutdownHook(new Thread(shipItemWorker::close));
        Runtime.getRuntime().addShutdownHook(new Thread(notApprovedWorker::close));
        shipItemWorker.start();
        notApprovedWorker.start();
    }
}
