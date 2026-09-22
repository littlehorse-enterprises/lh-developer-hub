package io.littlehorse.docs.variables;

import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.worker.LHTaskMethod;

public class VariableTasks {

    public record User(String email, String title, Long age) {}

    @LHTaskMethod("fetch-user")
    public User fetchUser(String userId) {
        if (userId.equals("obiwan")) {
            return new User("obiwan@jedi.temple", "Master Kenobi", 37L);
        }
        if (userId.equals("anakin")) {
            return new User("anakin@jedi.temple", "Padawan Skywalker (not Master)", 22L);
        }
        throw new LHTaskException("user-not-found", "Could not find specified user");
    }

    @LHTaskMethod("send-email")
    public String sendEmail(String toAddress, String message) {
        String result = "sent email " + message + " to address " + toAddress;
        System.out.println(result);
        return result;
    }
}