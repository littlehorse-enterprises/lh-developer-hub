package io.littlehorse.examples;

import io.littlehorse.sdk.usertask.annotations.UserTaskField;

public final class ApprovalForm {

    @UserTaskField(displayName = "Approved?", description = "Approve the purchase request.")
    public boolean approved;
}
