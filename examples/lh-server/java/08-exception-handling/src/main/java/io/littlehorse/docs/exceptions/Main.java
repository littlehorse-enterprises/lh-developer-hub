package io.littlehorse.docs.exceptions;

import java.util.List;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable price = wf.declareDouble("price").required();
        WfRunVariable itemId = wf.declareStr("item").withDefault("lightsaber");
        WfRunVariable userId = wf.declareStr("user-id").withDefault("obiwan");
        NodeOutput charge = wf.execute("charge-credit-card", userId, price);

        wf.handleException(charge, "insufficient-funds", handler -> {
            handler.execute("cancel-order-insufficient-funds", userId);
            handler.fail("insufficient-funds", "Credit card did not have sufficient funds");
        });
        wf.handleError(charge, handler -> {
            handler.execute("notify-order-failed", userId);
            handler.fail("technical-failure", "Failed to charge credit card");
        });
        wf.execute("ship-item", itemId, userId);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        OrderTasks tasks = new OrderTasks();
        List<LHTaskWorker> workers = List.of(
            new LHTaskWorker(tasks, "charge-credit-card", config),
            new LHTaskWorker(tasks, "ship-item", config),
            new LHTaskWorker(tasks, "cancel-order-insufficient-funds", config),
            new LHTaskWorker(tasks, "notify-order-failed", config));
        Workflow workflow = Workflow.newWorkflow("exception-example", Main::wfLogic);

        System.out.println("Run repeatedly with: lhctl run exception-example price 50.0");
        ExampleSupport.start(config, workers, workflow);
    }
}