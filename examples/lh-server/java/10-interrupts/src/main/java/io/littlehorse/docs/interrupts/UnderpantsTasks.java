package io.littlehorse.docs.interrupts;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class UnderpantsTasks {

    @LHTaskMethod("start-underpants-collection")
    public void startCollection() {
        System.out.println("Starting collection of underpants!");
    }

    @LHTaskMethod("collect-underpant")
    public String collectUnderpant(String underpantOwner) {
        String result = "Successfully collected underpant from " + underpantOwner;
        System.out.println(result);
        return result;
    }

    @LHTaskMethod("profit")
    public String profit(Object[] underpants) {
        String result = "Collected " + underpants.length + " underpants!";
        System.out.println(result);
        return result;
    }
}