package io.littlehorse.examples;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.proto.ExternalEventDefId;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.ListVariablesRequest;
import io.littlehorse.sdk.common.proto.PutExternalEventRequest;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.Variable;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.sdk.common.proto.WfRunId;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final LittleHorseBlockingStub client;

    public OrderController(LittleHorseBlockingStub client) {
        this.client = client;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    StartOrderResponse startOrder(@RequestBody StartOrderRequest request) {
        WfRun run = client.runWf(RunWfRequest.newBuilder()
                .setWfSpecName(OrderWorkflow.WF_SPEC_NAME)
                .putVariables("user-id", LHLibUtil.objToVarVal(request.userId()))
                .putVariables("item-id", LHLibUtil.objToVarVal(request.itemId()))
                .build());
        return new StartOrderResponse(run.getId().getId());
    }

    @PostMapping("/{wfRunId}/payment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void recordPayment(@PathVariable String wfRunId, @RequestBody PaymentRequest request) {
        client.putExternalEvent(PutExternalEventRequest.newBuilder()
                .setWfRunId(wfRunId(wfRunId))
                .setExternalEventDefId(
                        ExternalEventDefId.newBuilder().setName(OrderWorkflow.PAYMENT_EVENT))
                .setGuid(request.idempotencyKey())
                .setContent(LHLibUtil.objToVarVal(request.received()))
                .build());
    }

    @GetMapping("/{wfRunId}")
    OrderResponse getOrder(@PathVariable String wfRunId) {
        try {
            WfRun run = client.getWfRun(wfRunId(wfRunId));
            Map<String, Object> variables = new LinkedHashMap<>();
            for (Variable variable : client.listVariables(ListVariablesRequest.newBuilder()
                            .setWfRunId(run.getId())
                            .build())
                    .getResultsList()) {
                if (variable.getId().getThreadRunNumber() == 0) {
                    variables.put(
                            variable.getId().getName(),
                            LHLibUtil.varValToObj(variable.getValue(), Object.class));
                }
            }
            return new OrderResponse(run.getId().getId(), run.getStatus().name(), variables);
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order workflow not found");
            }
            throw ex;
        }
    }

    private static WfRunId wfRunId(String id) {
        return WfRunId.newBuilder().setId(id).build();
    }

    public record StartOrderRequest(String userId, String itemId) {}

    public record StartOrderResponse(String wfRunId) {}

    public record PaymentRequest(boolean received, String idempotencyKey) {}

    public record OrderResponse(String id, String status, Map<String, Object> variables) {}
}
