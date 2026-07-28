package org.example.workers;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class Greeter {
   
    @LHTaskMethod("greet")
    public String greet(String name){

        return "Hello there " + name;
    }
}
