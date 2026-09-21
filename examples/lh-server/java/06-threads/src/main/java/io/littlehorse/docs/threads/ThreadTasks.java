package io.littlehorse.docs.threads;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class ThreadTasks {

    @LHTaskMethod("my-task")
    public String myTask(String input) {
        String result = "Hello from a workflow thread: " + input;
        System.out.println(result);
        return result;
    }
}