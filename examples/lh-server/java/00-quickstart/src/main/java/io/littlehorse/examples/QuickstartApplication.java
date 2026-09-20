package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.CorrelatedEventConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.common.proto.ReturnType;
import io.littlehorse.sdk.common.proto.TypeDefinition;
import io.littlehorse.sdk.common.proto.VariableType;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class QuickstartApplication {

    public static void main(String[] args) {
        if (args.length != 1 || (!args[0].equals("register") && !args[0].equals("workers"))) {
            System.err.println("Please provide one argument: either 'register' or 'workers'");
            System.exit(1);
        }

        LHConfig config = new LHConfig();
        List<LHTaskWorker> workers = createWorkers(config);

        if (args[0].equals("register")) {
            registerMetadata(config.getBlockingStub(), workers);
        } else {
            startWorkers(workers);
        }
    }

    private static List<LHTaskWorker> createWorkers(LHConfig config) {
        QuickstartTasks tasks = new QuickstartTasks();
        return List.of(
                new LHTaskWorker(tasks, QuickstartWorkflow.VERIFY_IDENTITY_TASK, config),
                new LHTaskWorker(tasks, QuickstartWorkflow.NOTIFY_VERIFIED_TASK, config),
                new LHTaskWorker(tasks, QuickstartWorkflow.NOTIFY_NOT_VERIFIED_TASK, config));
    }

    private static void registerMetadata(LittleHorseBlockingStub client, List<LHTaskWorker> workers) {
        registerIdentityVerifiedEvent(client);
        workers.forEach(LHTaskWorker::registerTaskDef);
        Workflow workflow = Workflow.newWorkflow(QuickstartWorkflow.WF_SPEC_NAME, QuickstartWorkflow::quickstartWf);
        workflow.registerWfSpec(client);
        System.out.println("Registered TaskDefs, ExternalEventDef, and WfSpec " + QuickstartWorkflow.WF_SPEC_NAME);
    }

    private static void startWorkers(List<LHTaskWorker> workers) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));
        workers.forEach(LHTaskWorker::start);
        System.out.println("Task workers started. Press Ctrl+C to stop.");
    }

    private static void registerIdentityVerifiedEvent(LittleHorseBlockingStub client) {
        client.putExternalEventDef(PutExternalEventDefRequest.newBuilder()
                .setName(QuickstartWorkflow.IDENTITY_VERIFIED_EVENT)
                .setContentType(ReturnType.newBuilder()
                        .setReturnType(TypeDefinition.newBuilder().setPrimitiveType(VariableType.BOOL)))
                .setCorrelatedEventConfig(CorrelatedEventConfig.newBuilder().setDeleteAfterFirstCorrelation(false))
                .build());
    }
}
