package io.littlehorse.docs.structdefs;

import io.littlehorse.sdk.worker.LHStructDef;
import io.littlehorse.sdk.worker.LHStructField;

@LHStructDef("car")
public class Car {

    @LHStructField(description = "The vehicle manufacturer.")
    private String make;

    @LHStructField(description = "The manufacturer model name.")
    private String model;

    @LHStructField(description = "The model year.")
    private int year;

    public Car() {}

    public Car(String make, String model, int year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}