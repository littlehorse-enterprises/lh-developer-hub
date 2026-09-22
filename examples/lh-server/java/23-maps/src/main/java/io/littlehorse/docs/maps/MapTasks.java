package io.littlehorse.docs.maps;

import java.util.Map;

import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;

public class MapTasks {

    @LHTaskMethod("produce-map")
    @LHType(isLHMap = true)
    public Map<String, Long> produceMap() {
        return Map.of("alice", 10L, "bob", 20L);
    }
}