package io.littlehorse.examples;

import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.common.proto.VariableValue;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;

public class TaskFailureTasks {

    @LHTaskMethod(TaskFailuresExample.OPERATION_TASK)
    public String performOperation(String scenario, WorkerContext context) {
        int attempt = context.getAttemptNumber();
        context.log("perform-operation attempt=" + attempt + "; scenario=" + scenario);
        System.out.println("perform-operation attempt=" + attempt + ", scenario=" + scenario);

        if (TaskFailuresExample.SUCCESS_SCENARIO.equals(scenario)) {
            return "operation succeeded";
        }

        if (TaskFailuresExample.RETRYABLE_SCENARIO.equals(scenario)) {
            throw new RuntimeException("Transient dependency failure on attempt " + attempt);
        }

        if (TaskFailuresExample.BUSINESS_SCENARIO.equals(scenario)) {
            VariableValue content = VariableValue.newBuilder()
                    .setStr("No inventory remains for this request")
                    .build();
            LHTaskException exception = new LHTaskException(
                    TaskFailuresExample.BUSINESS_EXCEPTION_NAME,
                    TaskFailuresExample.BUSINESS_EXCEPTION_MESSAGE,
                    content);
            System.out.println("Business exception name=" + exception.getName()
                    + ", message=" + exception.getMessage()
                    + ", content=" + exception.getContent().getStr());
            throw exception;
        }

        return "unknown scenario: " + scenario;
    }

    @LHTaskMethod(TaskFailuresExample.RECOVER_TECHNICAL_TASK)
    public void recoverTechnicalError(WorkerContext context) {
        System.out.println("Recovered technical error on attempt " + context.getAttemptNumber());
    }

    @LHTaskMethod(TaskFailuresExample.RECOVER_BUSINESS_TASK)
    public void recoverBusinessException(
            String exceptionName, String exceptionMessage, String exceptionContent, WorkerContext context) {
        System.out.println("Handled business exception name=" + exceptionName
                + ", message=" + exceptionMessage
                + ", content=" + exceptionContent
                + ", handler attempt=" + context.getAttemptNumber());
    }

    @LHTaskMethod(TaskFailuresExample.FINISH_TASK)
    public void finishOperation(String scenario, WorkerContext context) {
        System.out.println("Finished scenario=" + scenario + " on attempt " + context.getAttemptNumber());
    }
}
