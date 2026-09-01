package io.littlehorse.examples;

import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;

public final class ShipmentWorkflow {

    public static final String WF_SPEC_NAME = "javalin-shipment";
    public static final String CREATE_LABEL_TASK = "create-shipping-label";
    public static final String DISPATCH_TASK = "dispatch-shipment";

    private ShipmentWorkflow() {}

    public static Workflow build() {
        Workflow workflow = Workflow.newWorkflow(WF_SPEC_NAME, wf -> {
            WfRunVariable shipmentId = wf.declareStr("shipment-id").required().searchable();
            WfRunVariable destination = wf.declareStr("destination").required();
            WfRunVariable shipmentStatus = wf.declareStr("shipment-status");

            shipmentStatus.assign(wf.execute(CREATE_LABEL_TASK, shipmentId, destination).withRetries(2));
            shipmentStatus.assign(wf.execute(DISPATCH_TASK, shipmentId, shipmentStatus));
        });

        workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                .setSecondsAfterWfTermination(14 * 24 * 60 * 60L)
                .build());
        return workflow;
    }
}
