package io.littlehorse.examples;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.CorrelatedEventConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.common.proto.ReturnType;
import io.littlehorse.sdk.common.proto.RunWfRequest;
import io.littlehorse.sdk.common.proto.TypeDefinition;
import io.littlehorse.sdk.common.proto.VariableType;
import io.littlehorse.sdk.common.proto.WfRun;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class QuickstartApplication {

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub client = config.getBlockingStub();
        QuickstartTasks tasks = new QuickstartTasks();
        List<LHTaskWorker> workers = List.of(
                new LHTaskWorker(tasks, QuickstartWorkflow.VERIFY_IDENTITY_TASK, config),
                new LHTaskWorker(tasks, QuickstartWorkflow.NOTIFY_VERIFIED_TASK, config),
                new LHTaskWorker(tasks, QuickstartWorkflow.NOTIFY_NOT_VERIFIED_TASK, config));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> workers.forEach(LHTaskWorker::close)));

        registerIdentityVerifiedEvent(client);
        workers.forEach(LHTaskWorker::registerTaskDef);
        Workflow workflow = Workflow.newWorkflow(QuickstartWorkflow.WF_SPEC_NAME, QuickstartWorkflow::quickstartWf)
                .withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                        .setSecondsAfterWfTermination(14 * 24 * 60 * 60L)
                        .build());
        workflow.registerWfSpec(client);
        workers.forEach(LHTaskWorker::start);

        WfRun sample = client.runWf(RunWfRequest.newBuilder()
                .setWfSpecName(QuickstartWorkflow.WF_SPEC_NAME)
                .putVariables("full-name", LHLibUtil.objToVarVal("Ada Lovelace"))
                .putVariables("email", LHLibUtil.objToVarVal("ada@example.com"))
                .putVariables("ssn", LHLibUtil.objToVarVal(123456789))
                .build());
        System.out.println("Started sample WfRun: " + sample.getId().getId());
        System.out.println("Send identity-verified for ada@example.com to complete the sample run.");
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
