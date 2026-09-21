package io.littlehorse.docs.structdefs;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.StructDefCompatibilityType;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable inputCar = wf.declareStruct("input-car", Car.class).required();
        wf.execute("describe-car", inputCar);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        CarTasks tasks = new CarTasks();
        LHTaskWorker worker = new LHTaskWorker(tasks, "describe-car", config);
        worker.registerStructDef(Car.class, StructDefCompatibilityType.NO_SCHEMA_UPDATES);
        Workflow workflow = Workflow.newWorkflow("structdef-example", Main::wfLogic);

        System.out.println("Run with: lhctl run structdef-example input-car '{\"make\":\"Incom\",\"model\":\"T-65 X-wing\",\"year\":1977}'");
        ExampleSupport.start(config, List.of(worker), workflow);
    }
}