package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class ExternalEventsWorker {

    @LHTaskMethod("record-approved")
    public String recordApproved(String orderId) {
        return "approval recorded for " + orderId;
    }

    @LHTaskMethod("record-rejected")
    public String recordRejected(String orderId) {
        return "rejection recorded for " + orderId;
    }

    @LHTaskMethod("record-timeout")
    public String recordTimeout(String orderId) {
        return "timeout recorded for " + orderId;
    }

    @LHTaskMethod("semantic-result")
    public String semanticResult(boolean approved, String orderId) {
        return (approved ? "APPROVED: " : "REJECTED: ") + orderId;
    }
}
