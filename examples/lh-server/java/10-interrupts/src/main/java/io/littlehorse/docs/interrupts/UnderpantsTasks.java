package io.littlehorse.docs.interrupts;

import io.littlehorse.sdk.worker.LHTaskMethod;
import java.util.List;

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
    public String profit(List<String> underpants) {
        String result = "Collected " + underpants.size() + " underpants!";
        System.out.println(result);
        return result;
    }
}