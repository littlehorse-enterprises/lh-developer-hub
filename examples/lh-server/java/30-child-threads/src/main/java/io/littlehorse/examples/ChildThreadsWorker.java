package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHType;
import io.littlehorse.sdk.worker.LHTaskMethod;

public class ChildThreadsWorker {

    @LHTaskMethod("fetch-erp-systems")
    @LHType(isLHArray = true)
    public String[] fetchErpSystems() {
        return new String[] {"sap", "netsuite", "dynamics"};
    }

    @LHTaskMethod("record-customer-in-erp")
    public String recordCustomerInErp(String customer, String erpSystem) {
        if ("dynamics".equalsIgnoreCase(erpSystem)) {
            throw new RuntimeException("Technical failure while processing the item.");
        }
        return "recorded " + customer + " in " + erpSystem;
    }

    @LHTaskMethod("notify-account-team")
    public String notifyAccountTeam(String customer) {
        return "account team notified for " + customer;
    }

    @LHTaskMethod("provision-customer-portal")
    public String provisionCustomerPortal(String customer) {
        return "portal provisioned for " + customer;
    }

    @LHTaskMethod("record-onboarding-failure")
    public String recordOnboardingFailure(String customer) {
        return "onboarding failure recorded for " + customer;
    }

    @LHTaskMethod("customer-onboarding-complete")
    public String customerOnboardingComplete(String customer) {
        return "customer onboarding complete for " + customer;
    }
}
