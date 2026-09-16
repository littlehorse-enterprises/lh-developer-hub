
import java.util.List;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.CorrelatedEventConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;



public class App {
   
    public static void main(String[] args) {
        LHConfig config = loadConfig();

        LittleHorseBlockingStub client = config.getBlockingStub();
        Workers taskMethods = new Workers();
        List<LHTaskWorker> workers = List.of(
            new LHTaskWorker(taskMethods, "assign-truck", config),
            new LHTaskWorker(taskMethods, "load-truck", config),
            new LHTaskWorker(taskMethods, "send-delay-email", config),
            new LHTaskWorker(taskMethods, "assign-driver", config),
            new LHTaskWorker(taskMethods, "out-for-delivery-notification", config)
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));
        workers.forEach(LHTaskWorker::registerTaskDef);

        Workflow workflow = Workflow.newWorkflow("logistics-workflow", wf ->{
            WfRunVariable destination = wf.declareStr("destination").required();
            WfRunVariable customerEmail = wf.declareStr("customer-email").required();
            WfRunVariable loadType = wf.declareStr("load-type").required();
            WfRunVariable loadId = wf.declareStr("load-id").required();
            WfRunVariable truckId = wf.declareStr("truck");
            WfRunVariable agentDecision = wf.declareStr("agent-decision");

            NodeOutput assignTruckOutput = wf.execute("assign-truck", loadType);
            truckId.assign(assignTruckOutput);

            wf.execute("load-truck", truckId, loadId);

            wf.waitForEvent("truck-loaded").withCorrelationId(truckId).withCorrelatedEventConfig(
                CorrelatedEventConfig.newBuilder()
                .setDeleteAfterFirstCorrelation(true)
                .build()
            ).registeredAs(String.class);

            NodeOutput agentDecisionOutput = wf.execute("check-weather", wf.format("check for weather at {0}", destination));
            agentDecision.assign(agentDecisionOutput);

            wf.doIf(agentDecision.isEqualTo("HOLD"), ifHandler ->{
                ifHandler.execute("send-delay-email", customerEmail);
            });
            wf.doWhile(agentDecision.isEqualTo("HOLD"), whileHandler ->{
                whileHandler.sleepSeconds(86400);
                NodeOutput agentOutput = wf.execute("check-weather", "Check weather for " + destination);
                agentDecision.assign(agentOutput); 
            });

            wf.execute("assign-driver", truckId);

            wf.execute("out-for-delivery-notification", customerEmail);
        });

        workflow.registerWfSpec(client);
        workers.forEach(LHTaskWorker::start);

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
