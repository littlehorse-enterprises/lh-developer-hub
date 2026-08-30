package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class OrderTasks {

    @LHTaskMethod(OrderWorkflow.PREPARE_ORDER_TASK)
    public String prepareOrder(String userId, String itemId) {
        System.out.printf("Preparing item %s for user %s%n", itemId, userId);
        return "AWAITING_PAYMENT";
    }

    @LHTaskMethod(OrderWorkflow.COMPLETE_ORDER_TASK)
    public String completeOrder(boolean paymentReceived) {
        return paymentReceived ? "COMPLETED" : "PAYMENT_REJECTED";
    }
}
