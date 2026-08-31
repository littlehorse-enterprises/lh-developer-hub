package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHType;
import java.util.Map;

public class AdvancedVariablesTasks {

    @LHTaskMethod(AdvancedVariablesWorkflow.LOAD_EXTRA_QUANTITY_TASK)
    @LHType(isLHArray = true)
    public Long[] loadExtraQuantity() {
        return new Long[] {1L};
    }

    @LHTaskMethod(AdvancedVariablesWorkflow.CALCULATE_SUBTOTAL_TASK)
    public double calculateSubtotal(
            @LHType(isLHArray = true) Long[] quantities, @LHType(isLHArray = true) Double[] unitPrices) {
        double subtotal = 0.0;
        int items = Math.min(quantities.length, unitPrices.length);
        for (int index = 0; index < items; index++) {
            subtotal += quantities[index] * unitPrices[index];
        }
        return subtotal;
    }

    @LHTaskMethod(AdvancedVariablesWorkflow.INSPECT_CART_TASK)
    public String inspectCart(
            @LHType(isLHArray = true) Long[] quantities,
            @LHType(isLHArray = true) Double[] unitPrices,
            @LHType(isLHMap = true) Map<String, Double> fees,
            long itemCount,
            double shipping) {
        String inspection = "items=" + itemCount + ", quantities=" + quantities.length + ", prices=" + unitPrices.length
                + ", shipping=" + shipping + ", fees=" + fees;
        System.out.println(inspection);
        return inspection;
    }

    @LHTaskMethod(AdvancedVariablesWorkflow.AUTOMATIC_DOUBLE_TASK)
    public double automaticDouble(double integerValue) {
        return integerValue * 1.0;
    }

    @LHTaskMethod(AdvancedVariablesWorkflow.APPLY_TIER_TASK)
    public double applyTier(int tier) {
        return tier == 1 ? 0.10 : 0.05;
    }

    @LHTaskMethod(AdvancedVariablesWorkflow.PARSE_CUSTOMER_NUMBER_TASK)
    public int parseCustomerNumber(int customerNumber) {
        return customerNumber;
    }

    @LHTaskMethod(AdvancedVariablesWorkflow.RECORD_INVALID_CAST_TASK)
    public String recordInvalidCast(String customerId) {
        return "Could not cast customer-id to INT: " + customerId;
    }

    @LHTaskMethod(AdvancedVariablesWorkflow.SHOW_FEATURED_SKU_TASK)
    public String showFeaturedSku(String sku) {
        return "Featured SKU: " + sku;
    }

    @LHTaskMethod(AdvancedVariablesWorkflow.PRINT_SUMMARY_TASK)
    public String printPricingSummary(String customerId, double total, String pricingStage, String secretNote, long unitCount) {
        String summary = "customer=" + customerId + ", total=" + total + ", stage=" + pricingStage + ", units=" + unitCount;
        System.out.println(summary + ", note-length=" + secretNote.length());
        return summary;
    }
}
