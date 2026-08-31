package io.littlehorse.examples;

import io.littlehorse.sdk.common.proto.VariableMutationType;
import io.littlehorse.sdk.common.proto.VariableType;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.TaskNodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import java.util.Map;

public final class AdvancedVariablesWorkflow {

    public static final String WF_SPEC_NAME = "advanced-pricing";
    public static final String LOAD_EXTRA_QUANTITY_TASK = "load-extra-quantity";
    public static final String CALCULATE_SUBTOTAL_TASK = "calculate-subtotal";
    public static final String INSPECT_CART_TASK = "inspect-cart";
    public static final String AUTOMATIC_DOUBLE_TASK = "automatic-double";
    public static final String APPLY_TIER_TASK = "apply-tier";
    public static final String PARSE_CUSTOMER_NUMBER_TASK = "parse-customer-number";
    public static final String RECORD_INVALID_CAST_TASK = "record-invalid-cast";
    public static final String SHOW_FEATURED_SKU_TASK = "show-featured-sku";
    public static final String PRINT_SUMMARY_TASK = "print-pricing-summary";

    private AdvancedVariablesWorkflow() {}

    public static Workflow build() {
        Workflow workflow = Workflow.newWorkflow(WF_SPEC_NAME, wf -> {
            WfRunVariable customerId = wf.declareStr("customer-id").required().searchable();
            WfRunVariable secretNote = wf.declareStr("secret-note").required().masked();
            WfRunVariable customerTier = wf.declareStr("customer-tier").withDefault("2").searchable();
            WfRunVariable unitCount = wf.declareInt("unit-count").withDefault(2);
            WfRunVariable quantities = wf.declareArray("quantities", Long.class)
                    .withDefault(new Long[] {2L, 1L, 3L});
            WfRunVariable unitPrices = wf.declareArray("unit-prices", Double.class)
                    .withDefault(new Double[] {19.99, 8.50, 3.25, 1.00});
            WfRunVariable fees = wf.declareMap("fees", String.class, Double.class)
                    .withDefault(Map.of("tax-rate", 0.0825, "shipping", 4.50));
            WfRunVariable featuredItem = wf.declareStruct("featured-item", CartItemPreview.class).required();
            WfRunVariable subtotal = wf.declareDouble("subtotal").searchable();
            WfRunVariable total = wf.declareDouble("total").searchable();
            WfRunVariable itemCount = wf.declareInt("item-count");
            WfRunVariable pricingStage = wf.declareStr("pricing-stage").withDefault("cart-received");

            quantities.assign(quantities.extend(wf.execute(LOAD_EXTRA_QUANTITY_TASK)));
            itemCount.assign(quantities.size());
            subtotal.assign(wf.execute(CALCULATE_SUBTOTAL_TASK, quantities, unitPrices));

            wf.execute(INSPECT_CART_TASK, quantities, unitPrices, fees, itemCount, fees.get("shipping"));
            wf.execute(AUTOMATIC_DOUBLE_TASK, itemCount);
            wf.execute(APPLY_TIER_TASK, customerTier.castTo(VariableType.INT));

            TaskNodeOutput invalidCast = wf.execute(
                    PARSE_CUSTOMER_NUMBER_TASK, customerId.castTo(VariableType.INT));
            wf.handleError(invalidCast, handler -> handler.execute(RECORD_INVALID_CAST_TASK, customerId));

            wf.execute(SHOW_FEATURED_SKU_TASK, featuredItem.get("sku"));
            total.assign(subtotal.multiply(fees.get("tax-rate").add(1)).add(fees.get("shipping")));
            wf.mutate(pricingStage, VariableMutationType.ASSIGN, "priced");
            wf.execute(PRINT_SUMMARY_TASK, customerId, total, pricingStage, secretNote, unitCount);
        });

        workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                .setSecondsAfterWfTermination(14 * 24 * 60 * 60L)
                .build());
        return workflow;
    }
}
