package io.littlehorse.docs.exceptions;

import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.worker.LHTaskMethod;
import java.util.concurrent.ThreadLocalRandom;

public class OrderTasks {

    @LHTaskMethod("charge-credit-card")
    public void chargeCreditCard(String userId, Double amount) {
        double currentBalance = ThreadLocalRandom.current().nextDouble(100);
        if (amount > currentBalance) {
            throw new LHTaskException("insufficient-funds", "User " + userId + " has insufficient funds");
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            throw new RuntimeException("Uh oh, network failure!");
        }
        System.out.println("Successfully charged credit card of user " + userId);
    }

    @LHTaskMethod("ship-item")
    public void shipItem(String itemId, String userId) {
        System.out.println("Successfully shipped item " + itemId + " to user " + userId);
    }

    @LHTaskMethod("cancel-order-insufficient-funds")
    public void cancelOrderInsufficientFunds(String userId) {
        System.out.println("Order canceled for " + userId + ": insufficient funds");
    }

    @LHTaskMethod("notify-order-failed")
    public void notifyOrderFailed(String userId) {
        System.out.println("Order failed for " + userId + ": technical failure");
    }
}