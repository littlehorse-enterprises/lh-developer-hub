package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.WorkerContext;

public class QuickstartTasks {

    @LHTaskMethod(QuickstartWorkflow.VERIFY_IDENTITY_TASK)
    public String verifyIdentity(String fullName, String email, int ssn) {
        return "Verification request accepted for " + fullName + " at " + email + " (SSN ending " + (ssn % 10000)
                + ")";
    }

    @LHTaskMethod(QuickstartWorkflow.NOTIFY_VERIFIED_TASK)
    public String notifyCustomerVerified(String fullName, String email, WorkerContext context) {
        context.log("Sending verified notification to " + email);
        return "Notified " + fullName + " that identity was verified";
    }

    @LHTaskMethod(QuickstartWorkflow.NOTIFY_NOT_VERIFIED_TASK)
    public String notifyCustomerNotVerified(String fullName, String email) {
        return "Notified " + fullName + " that identity was not verified";
    }
}
