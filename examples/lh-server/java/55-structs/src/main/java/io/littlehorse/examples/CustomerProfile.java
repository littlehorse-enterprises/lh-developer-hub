package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHStructDef;
import io.littlehorse.sdk.worker.LHStructField;

@LHStructDef("customer-profile")
public class CustomerProfile {

    private String customerId;
    private String displayName;

    @LHStructField(isNullable = true)
    private Address address;

    public CustomerProfile() {}

    public CustomerProfile(String customerId, String displayName, Address address) {
        this.customerId = customerId;
        this.displayName = displayName;
        this.address = address;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "%s (%s), %s".formatted(displayName, customerId, address);
    }
}
