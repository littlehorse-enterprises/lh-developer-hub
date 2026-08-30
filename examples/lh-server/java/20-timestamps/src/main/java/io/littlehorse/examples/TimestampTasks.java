package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;
import java.time.Instant;

public class TimestampTasks {

    @LHTaskMethod("format-timestamp")
    public String formatTimestamp(Instant eventTime) {
        return eventTime.toString();
    }

    @LHTaskMethod("print-timestamp")
    public void printTimestamp(String formattedTime) {
        System.out.println("Event timestamp: " + formattedTime);
    }
}
