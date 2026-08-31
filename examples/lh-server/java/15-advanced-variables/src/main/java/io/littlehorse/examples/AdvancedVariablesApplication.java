package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutStructDefRequest;
import io.littlehorse.sdk.common.proto.StructDefCompatibilityType;
import io.littlehorse.sdk.wfsdk.internal.structdefutil.LHStructDefType;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class AdvancedVariablesApplication {

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub client = config.getBlockingStub();
        AdvancedVariablesTasks tasks = new AdvancedVariablesTasks();
        List<LHTaskWorker> workers = List.of(
                new LHTaskWorker(tasks, AdvancedVariablesWorkflow.LOAD_EXTRA_QUANTITY_TASK, config),
                new LHTaskWorker(tasks, AdvancedVariablesWorkflow.CALCULATE_SUBTOTAL_TASK, config),
                new LHTaskWorker(tasks, AdvancedVariablesWorkflow.INSPECT_CART_TASK, config),
                new LHTaskWorker(tasks, AdvancedVariablesWorkflow.AUTOMATIC_DOUBLE_TASK, config),
                new LHTaskWorker(tasks, AdvancedVariablesWorkflow.APPLY_TIER_TASK, config),
                new LHTaskWorker(tasks, AdvancedVariablesWorkflow.PARSE_CUSTOMER_NUMBER_TASK, config),
                new LHTaskWorker(tasks, AdvancedVariablesWorkflow.RECORD_INVALID_CAST_TASK, config),
                new LHTaskWorker(tasks, AdvancedVariablesWorkflow.SHOW_FEATURED_SKU_TASK, config),
                new LHTaskWorker(tasks, AdvancedVariablesWorkflow.PRINT_SUMMARY_TASK, config));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        registerStructDef(client);
        workers.forEach(LHTaskWorker::registerTaskDef);
        AdvancedVariablesWorkflow.build().registerWfSpec(client);
        workers.forEach(LHTaskWorker::start);
        System.out.println("Registered advanced-pricing and started its task workers.");
    }

    private static void registerStructDef(LittleHorseBlockingStub client) {
        PutStructDefRequest request = new LHStructDefType(CartItemPreview.class)
                .toPutStructDefRequest()
                .toBuilder()
                .setAllowedUpdates(StructDefCompatibilityType.NO_SCHEMA_UPDATES)
                .build();
        client.putStructDef(request);
    }
}
