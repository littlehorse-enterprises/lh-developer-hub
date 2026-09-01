package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterruptTasks {

    private static final Logger log = LoggerFactory.getLogger(InterruptTasks.class);

    @LHTaskMethod("record-progress")
    public String recordProgress(Integer amount) {
        log.info("Recorded progress interrupt for {} unit(s)", amount);
        return "progress-recorded";
    }

    @LHTaskMethod("record-cancellation")
    public String recordCancellation(String reason) {
        log.info("Recorded cancellation interrupt: {}", reason);
        return "cancellation-recorded";
    }

    @LHTaskMethod("report-status")
    public String reportStatus(String status) {
        log.info("Interrupt demo reached terminal status {}", status);
        return status;
    }
}
