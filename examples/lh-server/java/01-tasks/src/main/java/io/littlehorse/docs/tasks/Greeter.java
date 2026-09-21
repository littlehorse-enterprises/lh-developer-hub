package io.littlehorse.docs.tasks;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class Greeter {

    @LHTaskMethod("greet")
    public String greeting(String name) {
        String result = "Hello there, " + name + "!";
        System.out.println(result);
        return result;
    }
}