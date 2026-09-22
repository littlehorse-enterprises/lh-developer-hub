package io.littlehorse.docs.structdefs;

import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;

public final class RegisterWorkflow {

    public static final String WF_NAME = "quickstart";

    private RegisterWorkflow() {}

    public static Workflow getWorkflow() {
        return Workflow.newWorkflow(WF_NAME, wf -> {
            WfRunVariable inputCar = wf.declareStruct("input-car", Car.class).required();
            wf.execute("describe-car", inputCar);
        });
    }
}