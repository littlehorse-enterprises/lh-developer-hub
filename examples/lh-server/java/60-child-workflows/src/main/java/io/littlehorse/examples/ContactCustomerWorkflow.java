package io.littlehorse.examples;

import io.littlehorse.sdk.wfsdk.ExternalEventNodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

public final class ContactCustomerWorkflow {

    public static final String NAME = "contact-customer";
    public static final String CUSTOMER_RESPONDED_EVENT = "customer-responded";

    private static final String EMAIL = "email";

    private ContactCustomerWorkflow() {}

    public static void define(WorkflowThread wf) {
        WfRunVariable eventId = wf.declareStr("event-id").required();
        WfRunVariable customerId = wf.declareStr("customer-id").required();
        WfRunVariable message = wf.declareStr("message").required();
        WfRunVariable contactMethod = wf.declareStr("contact-method").required();
        WfRunVariable response = wf.declareBool("response");

        wf.doIf(contactMethod.isEqualTo(EMAIL), email -> {
                    email.execute(ExampleTasks.SEND_EMAIL_TASK, customerId, message);
                    response.assign(waitForCustomerResponse(email, eventId));
                })
                .doElse(sms -> {
                    sms.execute(ExampleTasks.SEND_SMS_TASK, customerId, message);
                    response.assign(waitForCustomerResponse(sms, eventId));
                });

        wf.complete(response);
    }

    private static ExternalEventNodeOutput waitForCustomerResponse(WorkflowThread wf, WfRunVariable eventId) {
        return wf.waitForEvent(CUSTOMER_RESPONDED_EVENT)
                .withCorrelationId(eventId)
                .registeredAs(Boolean.class);
    }
}
