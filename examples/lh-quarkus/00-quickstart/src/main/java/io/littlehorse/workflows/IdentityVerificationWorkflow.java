package io.littlehorse.workflows;

import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.common.proto.LHErrorType;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

@LHWorkflow(IdentityVerificationWorkflow.IDENTITY_VERIFICATION_WORKFLOW)
public class IdentityVerificationWorkflow implements LHWorkflowDefinition {
    public static final String IDENTITY_VERIFICATION_WORKFLOW = "identity-verification";
    public static final String IDENTITY_VERIFIED_EVENT = "identity-verified";
    public static final String VERIFY_IDENTITY_TASK = "verify-identity";
    public static final String NOTIFY_CUSTOMER_VERIFIED_TASK = "notify-customer-verified";
    public static final String NOTIFY_CUSTOMER_NOT_VERIFIED_TASK = "notify-customer-not-verified";
    public static final String APPROVAL_STATUS = "approval-status";
    public static final String FULL_NAME = "full-name";
    public static final String EMAIL = "email";
    public static final String SSN = "ssn";
    public static final String IS_IDENTITY_VERIFIED = "is-identity-verified";

    @Override
    public void define(WorkflowThread wf) {
        WfRunVariable fullName = wf.declareStr(FULL_NAME).searchable().required();
        WfRunVariable email = wf.declareStr(EMAIL).searchable().required();
        WfRunVariable ssn = wf.declareInt(SSN).masked().required();
        WfRunVariable identityVerified = wf.declareBool(IS_IDENTITY_VERIFIED);
        WfRunVariable approvalStatus = wf.declareStr(APPROVAL_STATUS).withDefault("PENDING");

        wf.execute(VERIFY_IDENTITY_TASK, fullName, email, ssn).withRetries(3);

        NodeOutput identityVerificationResult = wf.waitForEvent(IDENTITY_VERIFIED_EVENT)
                .timeout(60 * 5)
                .withCorrelationId(email)
                .registeredAs(Boolean.class);

        wf.handleError(identityVerificationResult, LHErrorType.TIMEOUT, handler -> {
            approvalStatus.assign("REJECTED");
            handler.execute(NOTIFY_CUSTOMER_NOT_VERIFIED_TASK, fullName, email);
            handler.fail("customer-not-verified", "Unable to verify customer identity in time.");
        });

        identityVerified.assign(identityVerificationResult);

        wf.doIf(identityVerified.isEqualTo(true), ifBody -> {
                    approvalStatus.assign("APPROVED");
                    ifBody.execute(NOTIFY_CUSTOMER_VERIFIED_TASK, fullName, email);
                })
                .doElse(elseBody -> {
                    approvalStatus.assign("REJECTED");
                    elseBody.execute(NOTIFY_CUSTOMER_NOT_VERIFIED_TASK, fullName, email);
                });
    }
}
