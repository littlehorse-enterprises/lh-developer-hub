package org.example.workers;

import org.example.model.User;

import io.littlehorse.sdk.common.exception.LHTaskException;
import io.littlehorse.sdk.worker.LHTaskMethod;

public class Workers {

    // This Task Method returns a POJO, which is automatically serialized to a
    // JSON_OBJ variable value in LittleHorse.
    @LHTaskMethod("fetch-user")
    public User fetchUser(String userId) {
        if (userId.equals("obiwan")) {
            return new User("obiwan@jedi.temple", "Master Kenobi", 37);
        } else if (userId.equals("anakin")) {
            return new User("anakin@jedi.temple", "Padawan Skywalker (not Master)", 22);
        } else {
            throw new LHTaskException("user-not-found", "Could not find specified user");
        }
    }

    @LHTaskMethod("send-email")
    public String sendEmail(String toAddress, String message) {
        String result = "sent email " + message + " to address " + toAddress;
        System.out.println(result);
        return result;
    }
}
