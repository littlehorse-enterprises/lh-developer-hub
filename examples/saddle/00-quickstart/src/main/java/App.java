import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.worker.LHTaskMethod;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class App {
    public static void main(String[] args) {
        LHConfig config = loadConfig();
        App tasks = new App();
        LHTaskWorker worker = new LHTaskWorker(tasks, "send-slack-diy", config);

        worker.registerTaskDef();
        worker.start();
        Runtime.getRuntime().addShutdownHook(new Thread(worker::close));

        System.out.println("send-slack-diy worker started.");
    }

    @LHTaskMethod("send-slack-diy")
    public String sendSlackDiy(String message, String channel) {
        String output = "Sending message '" + message + "' to Slack channel '" + channel + "'.";
        System.out.println(output);
        return output;
    }

    private static LHConfig loadConfig() {
        Dotenv dotenv = Dotenv.load();
        Properties properties = new Properties();
        properties.setProperty(LHConfig.API_HOST_KEY, requireConfig(dotenv, LHConfig.API_HOST_KEY));
        properties.setProperty(LHConfig.API_PORT_KEY, requireConfig(dotenv, LHConfig.API_PORT_KEY));
        properties.setProperty(LHConfig.API_PROTOCOL_KEY, requireConfig(dotenv, LHConfig.API_PROTOCOL_KEY));
        properties.setProperty(LHConfig.TENANT_ID_KEY, requireConfig(dotenv, LHConfig.TENANT_ID_KEY));
        properties.setProperty(LHConfig.OAUTH_CLIENT_ID_KEY, requireConfig(dotenv, LHConfig.OAUTH_CLIENT_ID_KEY));
        properties.setProperty(LHConfig.OAUTH_CLIENT_SECRET_KEY, requireConfig(dotenv, LHConfig.OAUTH_CLIENT_SECRET_KEY));
        properties.setProperty(LHConfig.OAUTH_ACCESS_TOKEN_URL_KEY, requireConfig(dotenv, LHConfig.OAUTH_ACCESS_TOKEN_URL_KEY));
        return new LHConfig(properties);
    }

    private static String requireConfig(Dotenv dotenv, String name) {
        String value = dotenv.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required .env value: " + name);
        }
        return value;
    }
}