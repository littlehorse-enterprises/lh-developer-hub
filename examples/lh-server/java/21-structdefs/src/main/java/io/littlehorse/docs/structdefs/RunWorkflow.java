package io.littlehorse.docs.structdefs;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.VariableValue;

public final class RunWorkflow {

    private RunWorkflow() {}

    public static void run(LHConfig config) {
        Car inputCar = new Car("Pontiac", "Aztek", 2005);
        VariableValue inputCarValue = LHLibUtil.objToVarVal(inputCar);

        config.getBlockingStub().runWf(RunWfRequest.newBuilder()
            .setWfSpecName("quickstart")
            .putVariables("input-car", inputCarValue)
            .build());
    }
}