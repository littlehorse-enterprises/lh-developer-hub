package io.littlehorse.docs.tasks;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        Greeter greeter = new Greeter();
        LHTaskWorker worker = new LHTaskWorker(greeter, "greet", config);

        Runtime.getRuntime().addShutdownHook(new Thread(worker::close));
        worker.registerTaskDef();
        worker.start();
    }
}