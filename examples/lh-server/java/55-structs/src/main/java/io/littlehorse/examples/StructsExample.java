package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.StructDefCompatibilityType;
import io.littlehorse.sdk.common.proto.ThreadRetentionPolicy;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.LHStructBuilder;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StructsExample {

    public static final String WF_SPEC_NAME = "nested-structs-demo";

    private static final Logger log = LoggerFactory.getLogger(StructsExample.class);

    private StructsExample() {}

    public static Workflow buildWorkflow() {
        Workflow workflow = Workflow.newWorkflow(WF_SPEC_NAME, wf -> {
            WfRunVariable customerId = wf.declareStr("customer-id").required();
            WfRunVariable orderId = wf.declareStr("order-id").required();
            WfRunVariable sku = wf.declareStr("sku").required();
            WfRunVariable quantity = wf.declareInt("quantity").required();

            WfRunVariable customer = wf.declareStruct("customer", CustomerProfile.class);
            WfRunVariable order = wf.declareStruct("order", PurchaseOrder.class);

            NodeOutput addressOutput = wf.execute("lookup-address", customerId);
            NodeOutput normalizedAddress = wf.execute("normalize-address", addressOutput);

            LHStructBuilder customerValue = wf.buildStruct("customer-profile")
                    .put("customerId", customerId)
                    .put("displayName", wf.format("Customer {0}", customerId))
                    .put(
                            "address",
                            wf.buildInlineStruct()
                                    .put("street", normalizedAddress.get("street"))
                                    .put("city", normalizedAddress.get("city"))
                                    .put("state", normalizedAddress.get("state"))
                                    .put("postalCode", normalizedAddress.get("postalCode")));
            customer.assign(customerValue);

            // WfRunVariable.get() addresses nested fields without evaluating them in Java.
            wf.execute("audit-customer", customer.get("displayName"), customer.get("address").get("city"));

            LHStructBuilder orderValue = wf.buildStruct("purchase-order")
                    .put("orderId", orderId)
                    .put("customer", customer)
                    .put(
                            "lineItem",
                            wf.buildInlineStruct().put("sku", sku).put("quantity", quantity));
            order.assign(orderValue);

            wf.execute("save-order", order);
            wf.complete(order.get("orderId"));
        });

        return workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                        .setSecondsAfterWfTermination(24 * 60 * 60L)
                        .build())
                .withDefaultThreadRetentionPolicy(ThreadRetentionPolicy.newBuilder()
                        .setSecondsAfterThreadTermination(60 * 60L)
                        .build());
    }

    public static List<LHTaskWorker> createWorkers(LHConfig config) {
        StructTasks tasks = new StructTasks();
        List<LHTaskWorker> workers = List.of(
                new LHTaskWorker(tasks, "lookup-address", config),
                new LHTaskWorker(tasks, "normalize-address", config),
                new LHTaskWorker(tasks, "audit-customer", config),
                new LHTaskWorker(tasks, "save-order", config));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Closing nested struct demo workers");
            workers.forEach(LHTaskWorker::close);
        }));
        return workers;
    }

    private static void registerStructDefs(LHTaskWorker worker) {
        StructDefCompatibilityType compatibility = StructDefCompatibilityType.NO_SCHEMA_UPDATES;

        // Register dependencies before the StructDefs that contain them.
        worker.registerStructDef(Address.class, compatibility);
        worker.registerStructDef(CustomerProfile.class, compatibility);
        worker.registerStructDef(LineItem.class, compatibility);
        worker.registerStructDef(PurchaseOrder.class, compatibility);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        List<LHTaskWorker> workers = createWorkers(config);

        registerStructDefs(workers.get(0));
        workers.forEach(LHTaskWorker::registerTaskDef);
        buildWorkflow().registerWfSpec(config);
        workers.forEach(LHTaskWorker::start);
    }
}
