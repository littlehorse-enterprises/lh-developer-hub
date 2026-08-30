package io.littlehorse.examples;

import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

public final class OrderWorkflow {

    public static final String WF_SPEC_NAME = "spring-boot-order";
    public static final String PREPARE_ORDER_TASK = "prepare-order";
    public static final String COMPLETE_ORDER_TASK = "complete-order";
    public static final String REJECT_ORDER_TASK = "reject-order";
    public static final String PAYMENT_EVENT = "payment-received";

    private OrderWorkflow() {}

    private static WfRunVariable userId;
    private static WfRunVariable itemId;
    private static WfRunVariable orderStatus;

    private static NodeOutput paymentReceived;

    public static void orderWfLogic(WorkflowThread wf) {
        userId = wf.declareStr("user-id").required().searchable();
        itemId = wf.declareStr("item-id").required().searchable();
        orderStatus = wf.declareStr("order-status");

        orderStatus.assign(wf.execute(PREPARE_ORDER_TASK, userId, itemId));

        paymentReceived = wf.waitForEvent(PAYMENT_EVENT).registeredAs(Boolean.class);

        wf.doIf(paymentReceived.isEqualTo(true), OrderWorkflow::handleAccepted)
                .doElse(OrderWorkflow::handleRejected);
    }

    public static void handleAccepted(WorkflowThread wf) {
        orderStatus.assign(wf.execute(COMPLETE_ORDER_TASK, paymentReceived));
    }

    public static void handleRejected(WorkflowThread wf) {
        orderStatus.assign(wf.execute(REJECT_ORDER_TASK));
    }

    public static Workflow buildOrderWorkflow() {
        return Workflow.newWorkflow(WF_SPEC_NAME, OrderWorkflow::orderWfLogic);
    }
}
