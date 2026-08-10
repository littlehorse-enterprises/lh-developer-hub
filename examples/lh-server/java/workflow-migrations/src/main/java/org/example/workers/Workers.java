package org.example.workers;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class Workers {
   
    @LHTaskMethod("create-employee-record")
    public String createEmployeeRecord(){
        System.out.println("employee-record-created");
        return "employee-record-created";
    }

    @LHTaskMethod("send-onboarding-email")
    public String sendOnboardingEmail(){
        System.out.println("send-onboarding-email");
        return "send-onboarding-email";
    }

    @LHTaskMethod("grant-system-access")
    public String grantSystemAccess(String name){
        System.out.println("grant-system-access");
        return "grant-system-access " + name;
    }
}
