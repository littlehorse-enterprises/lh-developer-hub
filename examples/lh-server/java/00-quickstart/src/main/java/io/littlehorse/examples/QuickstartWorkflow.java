package io.littlehorse.examples;

import io.littlehorse.sdk.common.proto.LHErrorType;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

public final class QuickstartWorkflow {

    public static final String WF_SPEC_NAME = "quickstart-kyc";
    public static final String VERIFY_IDENTITY_TASK = "verify-identity";
    public static final String NOTIFY_VERIFIED_TASK = "notify-customer-verified";
    public static final String NOTIFY_NOT_VERIFIED_TASK = "notify-customer-not-verified";
    public static final String IDENTITY_VERIFIED_EVENT = "identity-verified";

    private QuickstartWorkflow() {}

    public static void quickstartWf(WorkflowThread wf) {
        WfRunVariable fullName = wf.declareStr("full-name").required().searchable();
        WfRunVariable email = wf.declareStr("email").required().searchable();
        WfRunVariable ssn = wf.declareInt("ssn").required().masked();
        WfRunVariable identityVerified = wf.declareBool("identity-verified").searchable();

        wf.execute(VERIFY_IDENTITY_TASK, fullName, email, ssn).withRetries(3);

        NodeOutput verification = wf.waitForEvent(IDENTITY_VERIFIED_EVENT)
                .timeout(300)
                .withCorrelationId(email, true)
                .registeredAs(Boolean.class);

        wf.handleError(verification, LHErrorType.TIMEOUT, handler -> {
            handler.execute(NOTIFY_NOT_VERIFIED_TASK, fullName, email);
            handler.fail("identity-verification-timeout", "Identity verification timed out.");
        });

        identityVerified.assign(verification);
        wf.doIf(identityVerified.isEqualTo(true), yes -> yes.execute(NOTIFY_VERIFIED_TASK, fullName, email))
                .doElse(no -> no.execute(NOTIFY_NOT_VERIFIED_TASK, fullName, email));
    }
}
