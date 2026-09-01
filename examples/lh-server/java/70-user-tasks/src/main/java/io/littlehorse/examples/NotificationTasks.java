package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;

public final class NotificationTasks {

    public static final String SEND_EMAIL_TASK = "user-task-send-email";

    @LHTaskMethod(SEND_EMAIL_TASK)
    public void sendEmail(String recipient, String message) {
        System.out.printf("Notification to %s: %s%n", recipient, message);
    }
}
