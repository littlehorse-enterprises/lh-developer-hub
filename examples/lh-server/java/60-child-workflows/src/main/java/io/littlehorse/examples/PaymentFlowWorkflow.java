package io.littlehorse.examples;

import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.SpawnedChildWf;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import java.util.Map;

public final class PaymentFlowWorkflow {

    public static final String NAME = "payment-flow";

    private static final String INVALID_PAYMENT_METHOD = "invalid";

    private PaymentFlowWorkflow() {}

    public static void define(WorkflowThread wf) {
        WfRunVariable eventId = wf.declareStr("event-id").required();
        WfRunVariable customerId = wf.declareStr("customer-id").required();
        WfRunVariable paymentMethod = wf.declareStr("payment-method").required();
        WfRunVariable contactMethod = wf.declareStr("contact-method").required();
        WfRunVariable amount = wf.declareInt("amount").required();

        WfRunVariable paymentSucceeded = wf.declareBool("payment-succeeded");
        NodeOutput authorization = wf.execute(ExampleTasks.AUTHORIZE_PAYMENT_TASK, paymentMethod, amount);
        paymentSucceeded.assign(authorization);

        wf.doIf(paymentSucceeded.isEqualTo(true), approved -> approved.execute(
                        ExampleTasks.CONFIRM_PAYMENT_TASK, amount))
                .doElse(rejected -> {
                    SpawnedChildWf contact = rejected.runWf(
                            ContactCustomerWorkflow.NAME,
                            Map.of(
                                    "event-id", eventId,
                                    "customer-id", customerId,
                                    "contact-method", contactMethod,
                                    "message", "Your payment method was rejected. Please update it."));
                    WfRunVariable customerResponded = rejected.declareBool("customer-responded");
                    customerResponded.assign(rejected.waitForChildWf(contact));
                    rejected.execute(
                            ExampleTasks.RECORD_PAYMENT_ISSUE_TASK, customerId, customerResponded);
                    paymentSucceeded.assign(false);
                });
        wf.complete(paymentSucceeded);
    }
}
