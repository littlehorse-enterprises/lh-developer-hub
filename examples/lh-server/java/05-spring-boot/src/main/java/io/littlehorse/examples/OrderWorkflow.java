package io.littlehorse.examples;

import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;

public final class OrderWorkflow {

    public static final String WF_SPEC_NAME = "spring-boot-order";
    public static final String PREPARE_ORDER_TASK = "prepare-order";
    public static final String COMPLETE_ORDER_TASK = "complete-order";
    public static final String PAYMENT_EVENT = "payment-received";

    private OrderWorkflow() {}

    public static Workflow build() {
        return Workflow.newWorkflow(WF_SPEC_NAME, wf -> {
            WfRunVariable userId = wf.declareStr("user-id").required().searchable();
            WfRunVariable itemId = wf.declareStr("item-id").required().searchable();
            WfRunVariable orderStatus = wf.declareStr("order-status");

            orderStatus.assign(wf.execute(PREPARE_ORDER_TASK, userId, itemId));

            NodeOutput paymentReceived = wf.waitForEvent(PAYMENT_EVENT).registeredAs(Boolean.class);
            orderStatus.assign(wf.execute(COMPLETE_ORDER_TASK, paymentReceived));
        });
    }
}
