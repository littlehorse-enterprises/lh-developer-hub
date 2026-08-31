package io.littlehorse.examples;

import io.littlehorse.sdk.usertask.annotations.UserTaskField;

public final class ManualErpEntryForm {

    @UserTaskField(displayName = "Customer entered", description = "Confirm the customer was entered manually.")
    public boolean customerEntered;
}
