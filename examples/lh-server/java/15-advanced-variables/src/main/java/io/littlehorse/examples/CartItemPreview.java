package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHStructDef;

@LHStructDef("cart-item-preview")
public class CartItemPreview {

    private String sku;
    private String description;

    public CartItemPreview() {}

    public CartItemPreview(String sku, String description) {
        this.sku = sku;
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
