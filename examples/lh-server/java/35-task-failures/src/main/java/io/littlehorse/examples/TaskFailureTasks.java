package io.littlehorse.examples;

import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.common.proto.VariableValue;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;
import java.util.concurrent.ThreadLocalRandom;

public class TaskFailureTasks {

    @LHTaskMethod(TaskFailuresExample.PAYMENT_TASK)
    public String makePayment(String creditCard, Long amount, WorkerContext context) {
        int attempt = context.getAttemptNumber();
        context.log("make-payment attempt=" + attempt + "; amount=" + amount);
        System.out.println("make-payment attempt=" + attempt + ", amount=" + amount);

        // Simulate a third-party payment API that is unavailable half the time.
        if (ThreadLocalRandom.current().nextBoolean()) {
            throw new RuntimeException("Payment API unavailable on attempt " + attempt);
        }

        if (TaskFailuresExample.INSUFFICIENT_FUNDS_CARD.equals(creditCard)
                || TaskFailuresExample.INVALID_CREDIT_CARD.equals(creditCard)) {
            String rejectionContent = TaskFailuresExample.INSUFFICIENT_FUNDS_CARD.equals(creditCard)
                    ? TaskFailuresExample.INSUFFICIENT_FUNDS_CONTENT
                    : TaskFailuresExample.INVALID_CREDIT_CARD_CONTENT;
            VariableValue content = VariableValue.newBuilder()
                    .setStr(rejectionContent)
                    .build();
            LHTaskException exception = new LHTaskException(
                    TaskFailuresExample.PAYMENT_REJECTED_EXCEPTION,
                    "The third-party payment API rejected the payment",
                    content);
            System.out.println("Payment exception name=" + exception.getName()
                    + ", message=" + exception.getMessage()
                    + ", content=" + exception.getContent().getStr());
            throw exception;
        }

        return "payment accepted";
    }

    @LHTaskMethod(TaskFailuresExample.RECOVER_TECHNICAL_TASK)
    public void recoverTechnicalError(WorkerContext context) {
        System.out.println("Payment API technical error recovered on attempt " + context.getAttemptNumber());
    }

    @LHTaskMethod(TaskFailuresExample.HANDLE_INSUFFICIENT_FUNDS_TASK)
    public void handleInsufficientFunds(String content, WorkerContext context) {
        System.out.println("Payment declined for insufficient funds: " + content);
    }

    @LHTaskMethod(TaskFailuresExample.HANDLE_INVALID_CREDIT_CARD_TASK)
    public void handleInvalidCreditCard(String content, WorkerContext context) {
        System.out.println("Payment declined for invalid credit card: " + content);
    }

    @LHTaskMethod(TaskFailuresExample.PROCESS_SHIPMENT_TASK)
    public void processShipment(Long amount, WorkerContext context) {
        System.out.println("Processed shipment for payment amount=" + amount
                + " on attempt " + context.getAttemptNumber());
    }
}
