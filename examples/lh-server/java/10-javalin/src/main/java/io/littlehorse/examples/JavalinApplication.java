package io.littlehorse.examples;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.ListVariablesRequest;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.Variable;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.sdk.common.proto.WfRunId;
import io.javalin.Javalin;
import java.util.LinkedHashMap;
import java.util.Map;

public class JavalinApplication {

    private static final int PORT = 8082;

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        ShipmentRuntime runtime = new ShipmentRuntime(config);
        runtime.registerAndStart();

        Javalin app = Javalin.create(javalinConfig ->
                javalinConfig.events(event -> event.serverStopping(runtime::close)));
        app.post("/shipments", ctx -> {
            ShipmentRequest request = ctx.bodyAsClass(ShipmentRequest.class);
            WfRun run = runtime.client().runWf(RunWfRequest.newBuilder()
                    .setWfSpecName(ShipmentWorkflow.WF_SPEC_NAME)
                    .putVariables("shipment-id", LHLibUtil.objToVarVal(request.shipmentId()))
                    .putVariables("destination", LHLibUtil.objToVarVal(request.destination()))
                    .build());
            ctx.status(201).json(Map.of("wfRunId", run.getId().getId()));
        });
        app.get("/shipments/{wfRunId}", ctx -> {
            try {
                ctx.json(shipmentResponse(runtime.client(), ctx.pathParam("wfRunId")));
            } catch (StatusRuntimeException ex) {
                if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
                    ctx.status(404).json(Map.of("error", "Shipment workflow not found"));
                } else {
                    throw ex;
                }
            }
        });
        app.start(PORT);
        System.out.println("Javalin shipment API listening on http://localhost:" + PORT);
    }

    private static Map<String, Object> shipmentResponse(LittleHorseBlockingStub client, String id) {
        WfRun run = client.getWfRun(WfRunId.newBuilder().setId(id).build());
        Map<String, Object> variables = new LinkedHashMap<>();
        for (Variable variable : client.listVariables(ListVariablesRequest.newBuilder()
                        .setWfRunId(run.getId())
                        .build())
                .getResultsList()) {
            if (variable.getId().getThreadRunNumber() == 0) {
                variables.put(variable.getId().getName(), LHLibUtil.varValToObj(variable.getValue(), Object.class));
            }
        }

        return Map.of("id", run.getId().getId(), "status", run.getStatus().name(), "variables", variables);
    }

    public record ShipmentRequest(String shipmentId, String destination) {}
}
