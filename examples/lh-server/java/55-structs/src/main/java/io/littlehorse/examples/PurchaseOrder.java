package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHStructDef;

@LHStructDef("purchase-order")
public class PurchaseOrder {

    private String orderId;
    private CustomerProfile customer;
    private LineItem lineItem;

    public PurchaseOrder() {}

    public PurchaseOrder(String orderId, CustomerProfile customer, LineItem lineItem) {
        this.orderId = orderId;
        this.customer = customer;
        this.lineItem = lineItem;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public CustomerProfile getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerProfile customer) {
        this.customer = customer;
    }

    public LineItem getLineItem() {
        return lineItem;
    }

    public void setLineItem(LineItem lineItem) {
        this.lineItem = lineItem;
    }

    @Override
    public String toString() {
        return "%s: %s, %s".formatted(orderId, customer, lineItem);
    }
}
