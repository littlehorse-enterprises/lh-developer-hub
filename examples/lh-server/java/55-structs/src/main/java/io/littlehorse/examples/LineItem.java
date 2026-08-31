package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHStructDef;

@LHStructDef("line-item")
public class LineItem {

    private String sku;
    private Integer quantity;

    public LineItem() {}

    public LineItem(String sku, Integer quantity) {
        this.sku = sku;
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "%s x%d".formatted(sku, quantity);
    }
}
