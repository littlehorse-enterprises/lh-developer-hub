package io.littlehorse.docs.threads;

import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;

public class ThreadTasks {

    @LHTaskMethod("my-task")
    public String myTask(String input, WorkerContext context) {
        int threadRunNumber = context.getNodeRunId().getThreadRunNumber();
        String threadName = threadRunNumber == 0 ? "parent" : "child";
        String result = "Hello from the " + threadName + " thread: " + input;
        System.out.println(result);
        return result;
    }
}