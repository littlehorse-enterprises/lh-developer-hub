package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class ChildThreadsWorker {

    @LHTaskMethod("process-item")
    public String processItem(String item) {
        if ("explode".equalsIgnoreCase(item)) {
            throw new RuntimeException("Technical failure while processing the item.");
        }
        return "processed: " + item;
    }

    @LHTaskMethod("fixed-child")
    public String fixedChild(String name) {
        return "completed: " + name;
    }

    @LHTaskMethod("record-technical-child-failure")
    public String recordTechnicalChildFailure() {
        return "technical child failure handled";
    }

    @LHTaskMethod("record-declined-child")
    public String recordDeclinedChild() {
        return "named child exception handled";
    }

    @LHTaskMethod("record-fixed-child-failure")
    public String recordFixedChildFailure() {
        return "fixed child failure handled";
    }

    @LHTaskMethod("all-children-complete")
    public String allChildrenComplete() {
        return "all child work complete";
    }
}
