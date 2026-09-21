package io.littlehorse.docs.variables;

import java.util.List;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.LHFormatString;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable userId = wf.declareStr("user-id").required().searchable();
        WfRunVariable userObject = wf.declareJsonObj("user-obj");
        WfRunVariable age = wf.declareInt("age");
        NodeOutput userOutput = wf.execute("fetch-user", userId);

        userObject.assign(userOutput);
        age.assign(userOutput.jsonPath("$.age"));
        LHFormatString message = wf.format(
                "Hello there, {0}! You are {1} years old",
                userObject.jsonPath("$.title"), age);
        wf.execute("send-email", userObject.jsonPath("$.email"), message);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        VariableTasks tasks = new VariableTasks();
        List<LHTaskWorker> workers = List.of(
            new LHTaskWorker(tasks, "fetch-user", config),
            new LHTaskWorker(tasks, "send-email", config));
        Workflow workflow = Workflow.newWorkflow("variables-example", Main::wfLogic);

        System.out.println("Run with: lhctl run variables-example user-id obiwan");
        ExampleSupport.start(config, workers, workflow);
    }
}