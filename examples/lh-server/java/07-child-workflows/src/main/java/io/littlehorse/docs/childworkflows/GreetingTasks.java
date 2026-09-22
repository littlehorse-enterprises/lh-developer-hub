package io.littlehorse.docs.childworkflows;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class GreetingTasks {

    @LHTaskMethod("greet")
    public String greet(String name) {
        String result = "Hello, " + name + "!";
        System.out.println(result);
        return result;
    }
}