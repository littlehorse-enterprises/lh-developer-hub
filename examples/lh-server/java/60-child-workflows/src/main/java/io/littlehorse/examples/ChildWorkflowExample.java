package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.SpawnedChildWf;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.time.Duration;
import java.util.Map;

public final class ChildWorkflowExample {

    private static final String PARENT_WF_NAME = "child-workflow-parent";
    private static final String CHILD_WF_NAME = "child-workflow-greeting";
    private static final String GREET_TASK = "child-workflow-greet";

    private ChildWorkflowExample() {}

    private static Workflow configure(Workflow workflow) {
        workflow.setDefaultTaskRetries(1);
        return workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                .setSecondsAfterWfTermination(Duration.ofDays(14).toSeconds())
                .build());
    }

    public static void childWf(WorkflowThread wf) {
        WfRunVariable childName = wf.declareStr("child-name").required();
        wf.complete(wf.execute(GREET_TASK, childName));
    }

    public static void parentWf(WorkflowThread wf) {
        WfRunVariable parentName = wf.declareStr("name").required();
        WfRunVariable childGreeting = wf.declareStr("child-greeting");

        // runWf starts the child; waitForChildWf consumes its eventual output.
        SpawnedChildWf child = wf.runWf(CHILD_WF_NAME, Map.of("child-name", parentName));
        NodeOutput childOutput = wf.waitForChildWf(child);
        childGreeting.assign(childOutput);
        wf.complete(wf.execute(GREET_TASK, childGreeting));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();

        LHTaskWorker worker = new LHTaskWorker(new GreetingTasks(), GREET_TASK, config);
        worker.registerTaskDef();

        // The child WfSpec is registered before the parent references it.
        Workflow childWorkflow = configure(Workflow.newWorkflow(CHILD_WF_NAME, ChildWorkflowExample::childWf));
        Workflow parentWorkflow = configure(Workflow.newWorkflow(PARENT_WF_NAME, ChildWorkflowExample::parentWf));
        childWorkflow.registerWfSpec(config);
        parentWorkflow.registerWfSpec(config);

        Runtime.getRuntime().addShutdownHook(new Thread(worker::close));
        worker.start();

        System.out.println("Registered child-workflow-greeting and child-workflow-parent.");
        System.out.println("Run child-workflow-parent with lhctl to start a child WfRun.");
    }

    public static final class GreetingTasks {

        @io.littlehorse.sdk.worker.LHTaskMethod(GREET_TASK)
        public String greet(String name) {
            String result = "hello from child workflow, " + name;
            System.out.println("child-workflow-greet -> " + result);
            return result;
        }
    }
}
