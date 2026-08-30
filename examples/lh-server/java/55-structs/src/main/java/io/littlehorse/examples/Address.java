package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHStructDef;

@LHStructDef("address")
public class Address {

    private String street;
    private String city;
    private String state;
    private Integer postalCode;

    public Address() {}

    public Address(String street, String city, String state, Integer postalCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return "%s, %s, %s %d".formatted(street, city, state, postalCode);
    }
}
