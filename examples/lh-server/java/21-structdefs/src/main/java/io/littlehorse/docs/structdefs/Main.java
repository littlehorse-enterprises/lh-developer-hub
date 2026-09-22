package io.littlehorse.docs.structdefs;

import java.util.List;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        if (args.length == 1 && args[0].equals("run")) {
            RunWorkflow.run(config);
            return;
        }

        LHStructDefType lhStructDefType = new LHStructDefType(Car.class);
        config.getBlockingStub().putStructDef(lhStructDefType.toPutStructDefRequest());

        CarTaskWorker tasks = new CarTaskWorker();
        LHTaskWorker worker = new LHTaskWorker(tasks, "describe-car", config);
        Workflow workflow = RegisterWorkflow.getWorkflow();

        System.out.println("Run in another terminal with: ./gradlew :examples:lh-server:java:21-structdefs:run --args=run");
        ExampleSupport.start(config, List.of(worker), workflow);
    }
}