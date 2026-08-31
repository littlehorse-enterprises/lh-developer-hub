package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class ShipmentTasks {

    @LHTaskMethod(ShipmentWorkflow.CREATE_LABEL_TASK)
    public String createLabel(String shipmentId, String destination) {
        System.out.printf("Created label for %s to %s%n", shipmentId, destination);
        return "LABEL_CREATED";
    }

    @LHTaskMethod(ShipmentWorkflow.DISPATCH_TASK)
    public String dispatchShipment(String shipmentId, String shipmentStatus) {
        System.out.printf("Dispatching %s after %s%n", shipmentId, shipmentStatus);
        return "DISPATCHED";
    }
}
