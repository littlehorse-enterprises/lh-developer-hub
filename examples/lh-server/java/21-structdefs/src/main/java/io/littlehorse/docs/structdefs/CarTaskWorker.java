package io.littlehorse.docs.structdefs;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class CarTaskWorker {

    @LHTaskMethod("describe-car")
    public String describeCar(Car car) {
        return "You drive a " + car.getMake() + " " + car.getModel();
    }
}