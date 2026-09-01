package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class ConditionalTasks {

    @LHTaskMethod("record-request")
    public void recordRequest(long amount, boolean expedited) {
        System.out.println("Request amount=" + amount + ", expedited=" + expedited);
    }

    @LHTaskMethod("route-large")
    public void routeLarge(long amount) {
        System.out.println("Routing large request: " + amount);
    }

    @LHTaskMethod("validate-large-request")
    public void validateLargeRequest(long amount) {
        System.out.println("Validating large request: " + amount);
    }

    @LHTaskMethod("notify-large-request")
    public void notifyLargeRequest(long amount) {
        System.out.println("Notifying large request owner: " + amount);
    }

    @LHTaskMethod("route-expedited")
    public void routeExpedited(long amount) {
        System.out.println("Routing expedited request: " + amount);
    }

    @LHTaskMethod("route-standard")
    public void routeStandard(long amount) {
        System.out.println("Routing standard request: " + amount);
    }

    @LHTaskMethod("finish-request")
    public void finishRequest() {
        System.out.println("Request processing finished");
    }
}
