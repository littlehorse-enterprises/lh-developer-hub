package io.littlehorse.examples;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.CorrelatedEventConfig;
import io.littlehorse.sdk.common.proto.ExternalEventDefId;
import io.littlehorse.sdk.common.proto.LHErrorType;
import io.littlehorse.sdk.common.proto.PutCorrelatedEventRequest;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.ThreadRetentionPolicy;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.sdk.wfsdk.ExternalEventNodeOutput;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public final class ExternalEventsExample {

    private static final String WORKFLOW_NAME = "external-events";
    private static final String EVENT_NAME = "approval-event";
    private static final int EVENT_TIMEOUT_SECONDS = 15;

    private ExternalEventsExample() {}

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable orderId = wf.declareStr("order-id").required();
        ExternalEventNodeOutput event = wf.waitForEvent(EVENT_NAME)
                .timeout(EVENT_TIMEOUT_SECONDS)
                .withCorrelationId(orderId, true)
                .withCorrelatedEventConfig(CorrelatedEventConfig.newBuilder()
                        .setDeleteAfterFirstCorrelation(true)
                        .build())
                .registeredAs(Boolean.class);

        wf.handleError(event, LHErrorType.TIMEOUT, handler -> {
            handler.execute("record-timeout", orderId);
            handler.fail("approval-timeout", "The approval event did not arrive before the timeout.");
        });

        WfRunVariable approved = wf.declareBool("approved");
        approved.assign(event);
        wf.doIf(approved.isEqualTo(true), yes -> yes.execute("record-approved", orderId))
                .doElse(no -> no.execute("record-rejected", orderId));

        wf.complete(wf.execute("semantic-result", approved, orderId));
    }

    private static List<LHTaskWorker> getTaskWorkers(LHConfig config) {
        ExternalEventsWorker executable = new ExternalEventsWorker();
        return List.of(
                new LHTaskWorker(executable, "record-approved", config),
                new LHTaskWorker(executable, "record-rejected", config),
                new LHTaskWorker(executable, "record-timeout", config),
                new LHTaskWorker(executable, "semantic-result", config));
    }

    private static PutCorrelatedEventRequest eventRequest(String key, boolean approved) {
        return PutCorrelatedEventRequest.newBuilder()
                .setExternalEventDefId(ExternalEventDefId.newBuilder().setName(EVENT_NAME))
                .setKey(key)
                .setContent(LHLibUtil.objToVarVal(approved))
                .build();
    }

    private static WfRun runWorkflow(LHConfig config, String orderId) {
        return config.getBlockingStub().runWf(RunWfRequest.newBuilder()
                .setWfSpecName(WORKFLOW_NAME)
                .putVariables("order-id", LHLibUtil.objToVarVal(orderId))
                .build());
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        List<LHTaskWorker> workers = getTaskWorkers(config);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        workers.forEach(LHTaskWorker::registerTaskDef);
        Workflow workflow = Workflow.newWorkflow(WORKFLOW_NAME, ExternalEventsExample::wfLogic)
                .withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                        .setSecondsAfterWfTermination(14 * 24 * 60 * 60L)
                        .build())
                .withDefaultThreadRetentionPolicy(ThreadRetentionPolicy.newBuilder()
                        .setSecondsAfterThreadTermination(7 * 24 * 60 * 60L)
                        .build());
        workflow.registerWfSpec(config.getBlockingStub());

        String beforeRunKey = args.length > 0 ? args[0] : "before-run-demo-" + System.currentTimeMillis();
        String afterRunKey = args.length > 1 ? args[1] : "after-run-demo-" + System.currentTimeMillis();

        config.getBlockingStub().putCorrelatedEvent(eventRequest(beforeRunKey, true));
        WfRun beforeRun = runWorkflow(config, beforeRunKey);

        WfRun afterRun = runWorkflow(config, afterRunKey);
        config.getBlockingStub().putCorrelatedEvent(eventRequest(afterRunKey, false));

        System.out.println("Before-run event example:");
        System.out.println(LHLibUtil.protoToJson(beforeRun));
        System.out.println("After-run event example:");
        System.out.println(LHLibUtil.protoToJson(afterRun));

        workers.forEach(LHTaskWorker::start);
    }
}
