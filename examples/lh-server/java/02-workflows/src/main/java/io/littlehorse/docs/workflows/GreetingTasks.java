package io.littlehorse.docs.workflows;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class GreetingTasks {

    @LHTaskMethod("greet")
    public String greeting(String name) {
        String result = "Hello " + name + "!";
        System.out.println(result);
        return result;
    }
}