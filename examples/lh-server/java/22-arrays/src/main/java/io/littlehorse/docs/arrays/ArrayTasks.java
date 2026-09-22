package io.littlehorse.docs.arrays;

import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

public class ArrayTasks {

    @LHTaskMethod("produce-array")
    @LHType(isLHArray = true)
    public Long[] produceArray() {
        return new Long[] {1L, 2L, 3L};
    }

    @LHTaskMethod("process-item")
    public void processItem(Long item) {
        System.out.println("Processed " + item);
    }

    @LHTaskMethod("consume-array")
    public String consumeArray(@LHType(isLHArray = true) Long[] arr) {
        return "Received " + arr.length + " elements";
    }
}