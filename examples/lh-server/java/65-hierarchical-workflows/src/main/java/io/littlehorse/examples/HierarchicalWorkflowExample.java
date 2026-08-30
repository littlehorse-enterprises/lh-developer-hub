package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.time.Duration;

public final class HierarchicalWorkflowExample {

    private static final String PARENT_WF_NAME = "hierarchical-parent";
    private static final String CHILD_WF_NAME = "hierarchical-child";
    private static final String GRANDCHILD_WF_NAME = "hierarchical-grandchild";
    private static final String GREET_TASK = "hierarchical-greet";

    private HierarchicalWorkflowExample() {}

    private static Workflow configure(Workflow workflow) {
        workflow.setDefaultTaskRetries(1);
        return workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                .setSecondsAfterWfTermination(Duration.ofDays(14).toSeconds())
                .build());
    }

    public static void parentWf(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name").required().asPublic();
        wf.complete(wf.execute(GREET_TASK, name));
    }

    public static void childWf(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name").asInherited();
        NodeOutput greeting = wf.execute(GREET_TASK, name);
        name.assign("updated-by-child");
        wf.complete(greeting);
    }

    public static void grandchildWf(WorkflowThread wf) {
        WfRunVariable name = wf.declareStr("name").asInherited();
        wf.complete(wf.execute(GREET_TASK, name));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();

        LHTaskWorker worker = new LHTaskWorker(new GreetingTasks(), GREET_TASK, config);
        worker.registerTaskDef();

        // Parent metadata must exist before a WfSpec can refer to it.
        Workflow parentWorkflow = configure(Workflow.newWorkflow(PARENT_WF_NAME, HierarchicalWorkflowExample::parentWf));
        Workflow childWorkflow = configure(Workflow.newWorkflow(CHILD_WF_NAME, HierarchicalWorkflowExample::childWf));
        childWorkflow.setParent(PARENT_WF_NAME);
        Workflow grandchildWorkflow = configure(
                Workflow.newWorkflow(GRANDCHILD_WF_NAME, HierarchicalWorkflowExample::grandchildWf));
        grandchildWorkflow.setParent(CHILD_WF_NAME);
        parentWorkflow.registerWfSpec(config);
        childWorkflow.registerWfSpec(config);
        grandchildWorkflow.registerWfSpec(config);

        Runtime.getRuntime().addShutdownHook(new Thread(worker::close));
        worker.start();

        System.out.println("Registered hierarchical-parent, hierarchical-child, and hierarchical-grandchild.");
        System.out.println("Run them with lhctl and --parentWfRunId as shown in the README.");
    }

    public static final class GreetingTasks {

        @io.littlehorse.sdk.worker.LHTaskMethod(GREET_TASK)
        public String greet(String name) {
            String result = "hello from hierarchy, " + name;
            System.out.println("hierarchical-greet -> " + result);
            return result;
        }
    }
}
