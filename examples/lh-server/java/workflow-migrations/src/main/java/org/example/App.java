package org.example;

import org.example.workers.Workers;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.ApplyWorkflowMigrationPlanRequest;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.MigrationVars;
import io.littlehorse.sdk.common.proto.NodeMigrationPlan;
import io.littlehorse.sdk.common.proto.PutExternalEventDefRequest;
import io.littlehorse.sdk.common.proto.PutWorkflowMigrationPlanRequest;
import io.littlehorse.sdk.common.proto.ThreadMigrationPlanRequest;
import io.littlehorse.sdk.common.proto.VariableAssignment;
import io.littlehorse.sdk.common.proto.VariableValue;
import io.littlehorse.sdk.common.proto.WfRunId;
import io.littlehorse.sdk.common.proto.WfSpecId;
import io.littlehorse.sdk.common.proto.WorkflowMigrationPlanId;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class App {

    private static final String PLAN_NAME = "onboarding-migration-plan";
    private static final String WF_NAME = "onboarding-workflow-w-variable";

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];
        switch (command) {
            case "step1":
                runStep1RegisterAndStartWorkers();
                break;
            case "step2":
                runStep2RegisterMigrationPlan();
                break;
            case "step3":
                if (args.length < 2) {
                    System.out.println("step3 requires <wfRunId>");
                    printUsage();
                    return;
                }
                runStep3ApplyMigration(args[1]);
                break;
            case "step4":
                if (args.length < 2) {
                    System.out.println("step4 requires <wfRunId>");
                    printUsage();
                    return;
                }
                runStep4ApplyMigrationWithVariable(args[1]);
                break;
            default:
                System.out.println("Unknown command: " + command);
                printUsage();
        }
    }

    private static void runStep1RegisterAndStartWorkers() {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub stub = config.getBlockingStub();

        Workers workers = new Workers();
        LHTaskWorker onboardingEmailWorker = new LHTaskWorker(workers, "send-onboarding-email", config);
        LHTaskWorker grantSystemAccessWorker = new LHTaskWorker(workers, "grant-system-access", config);
        LHTaskWorker createEmployeeRecordWorker = new LHTaskWorker(workers, "create-employee-record", config);

        onboardingEmailWorker.registerTaskDef();
        grantSystemAccessWorker.registerTaskDef();
        createEmployeeRecordWorker.registerTaskDef();

        Workflow oldSpec = Workflow.newWorkflow(WF_NAME, wf -> {
            wf.declareStr("name");
            wf.execute("create-employee-record");
            wf.execute("send-onboarding-email");
            wf.waitForEvent("sign-employee-agreement");
            wf.waitForEvent("training-complete");
        });

        Workflow newSpec = Workflow.newWorkflow(WF_NAME, wf -> {
            WfRunVariable name = wf.declareStr("name");
            wf.execute("create-employee-record");
            wf.execute("send-onboarding-email");
            wf.waitForEvent("sign-employee-agreement");
            wf.waitForEvent("training-complete");
            wf.execute("grant-system-access", name);
        });

        stub.putExternalEventDef(PutExternalEventDefRequest.newBuilder().setName("sign-employee-agreement").build());
        stub.putExternalEventDef(PutExternalEventDefRequest.newBuilder().setName("training-complete").build());
        oldSpec.registerWfSpec(config);
        newSpec.registerWfSpec(config);

        onboardingEmailWorker.start();
        grantSystemAccessWorker.start();
        createEmployeeRecordWorker.start();

        Runtime.getRuntime().addShutdownHook(new Thread(onboardingEmailWorker::close));
        Runtime.getRuntime().addShutdownHook(new Thread(grantSystemAccessWorker::close));
        Runtime.getRuntime().addShutdownHook(new Thread(createEmployeeRecordWorker::close));

        System.out.println("Step 1 complete: task defs, event defs, wfSpecs, and workers registered.");
    }

    private static void runStep2RegisterMigrationPlan() {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub stub = config.getBlockingStub();

        PutWorkflowMigrationPlanRequest request = PutWorkflowMigrationPlanRequest.newBuilder()
                .setName(PLAN_NAME)
                .setOldWfSpec(WfSpecId.newBuilder().setName(WF_NAME).setMajorVersion(0).setRevision(0).build())
                .setMajorVersion(0)
                .setRevision(1)
                .putThreadMigrations(
                        "entrypoint",
                        ThreadMigrationPlanRequest.newBuilder()
                                .setNewThreadName("entrypoint")
                                .putNodeMigrations(
                                        "4-training-complete-EXTERNAL_EVENT",
                                        NodeMigrationPlan.newBuilder()
                                                .setNewNodeName("4-training-complete-EXTERNAL_EVENT")
                                                .build())
                                .putNodeMigrations(
                                        "3-sign-employee-agreement-EXTERNAL_EVENT",
                                        NodeMigrationPlan.newBuilder()
                                                .setNewNodeName("3-sign-employee-agreement-EXTERNAL_EVENT")
                                                .build())
                                .build())
                .build();

        stub.putWorkflowMigrationPlan(request);

        System.out.println("Step 2 complete: migration plan registered.");
    }

    private static void runStep3ApplyMigration(String wfRunId) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub stub = config.getBlockingStub();

        ApplyWorkflowMigrationPlanRequest req = ApplyWorkflowMigrationPlanRequest.newBuilder()
                .setId(WorkflowMigrationPlanId.newBuilder().setName(PLAN_NAME).build())
                .setWfRunId(WfRunId.newBuilder().setId(wfRunId).build())
                .build();

        stub.applyWorkflowMigrationPlan(req);
        System.out.println("Step 3 complete: migration applied to WfRun " + wfRunId);
    }

    private static void runStep4ApplyMigrationWithVariable(String wfRunId) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub stub = config.getBlockingStub();

        MigrationVars migrationVars = MigrationVars.newBuilder()
                .putVarAssignmentByVarName(
                        "name",
                        VariableAssignment.newBuilder()
                            .setLiteralValue(VariableValue.newBuilder().setStr("obi-wan").build())
                                .build())
                .build();

        ApplyWorkflowMigrationPlanRequest req = ApplyWorkflowMigrationPlanRequest.newBuilder()
                .setId(WorkflowMigrationPlanId.newBuilder().setName(PLAN_NAME).build())
                .setWfRunId(WfRunId.newBuilder().setId(wfRunId).build())
                .putMigrationVarsByThread("entrypoint", migrationVars)
                .build();

        stub.applyWorkflowMigrationPlan(req);
        System.out.println("Step 4 complete: migration with variable override applied to WfRun " + wfRunId);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  step1");
        System.out.println("    Register task defs, event defs, old/new WfSpec versions, and start workers.");
        System.out.println("  step2");
        System.out.println("    Register the workflow migration plan.");
        System.out.println("  step3 <wfRunId>");
        System.out.println("    Apply migration plan to an existing WfRun.");
        System.out.println("  step4 <wfRunId>");
        System.out.println("    Apply migration plan and override variable 'name' on destination thread.");
    }
}
