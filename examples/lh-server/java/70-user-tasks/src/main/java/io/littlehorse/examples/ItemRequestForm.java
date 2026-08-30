package io.littlehorse.examples;

import io.littlehorse.sdk.usertask.annotations.UserTaskField;

public final class ItemRequestForm {

    @UserTaskField(displayName = "Requested item", description = "What should IT purchase?")
    public String requestedItem;

    @UserTaskField(displayName = "Justification", description = "Why is this item needed?")
    public String justification;
}
