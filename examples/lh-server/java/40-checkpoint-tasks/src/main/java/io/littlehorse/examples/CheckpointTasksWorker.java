package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;

public class CheckpointTasksWorker {

    @LHTaskMethod("checkpointed-side-effect")
    public String checkpointedSideEffect(String name, WorkerContext context) {
        int attemptNumber = context.getAttemptNumber();
        System.out.printf(
                "metadata: wfRun=%s taskRun=%s attempt=%d%n",
                context.getWfRunId(),
                context.getTaskRunId().getTaskGuid(),
                attemptNumber);

        String sideEffectReceipt = context.executeAndCheckpoint(
                checkpointContext -> {
                    checkpointContext.log("sending the greeting side effect");
                    System.out.println("SIDE EFFECT: send greeting for " + name);
                    return "receipt-for-" + name;
                },
                String.class);

        System.out.println("after checkpoint on attempt " + attemptNumber);
        if (attemptNumber == 0) {
            throw new RuntimeException("Intentional post-checkpoint failure on the first attempt.");
        }

        return sideEffectReceipt + "; completed on attempt " + attemptNumber;
    }
}
