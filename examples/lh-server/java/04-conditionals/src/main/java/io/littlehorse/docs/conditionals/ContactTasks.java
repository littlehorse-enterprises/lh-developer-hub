package io.littlehorse.docs.conditionals;

import io.littlehorse.sdk.worker.LHTaskMethod;
import java.util.Set;

public class ContactTasks {

    @LHTaskMethod("fetch-contact-method")
    public String fetchContactMethod(String userId) {
        return Set.of("obiwan", "padme", "satine").contains(userId) ? "COMLINK" : "HOLOGRAM";
    }

    @LHTaskMethod("send-comlink-message")
    public String sendComlink(String userId, String message) {
        return report("comlink", userId, message);
    }

    @LHTaskMethod("send-hologram")
    public String sendHologram(String userId, String message) {
        return report("hologram", userId, message);
    }

    private String report(String method, String userId, String message) {
        String result = "sent " + method + " " + message + " to user " + userId;
        System.out.println(result);
        return result;
    }
}