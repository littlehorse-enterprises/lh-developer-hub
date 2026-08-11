package org.example;

import org.example.workers.Workers;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.wfsdk.LHFormatString;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class App {

    public static final String WF_NAME = "variables-example";

    public static void wfLogic(WorkflowThread wf) {
        // user-id is required as input and searchable so we can find WfRuns by it.
        WfRunVariable userId = wf.declareStr("user-id").required().searchable();
        WfRunVariable userObject = wf.declareJsonObj("user-obj");
        WfRunVariable age = wf.declareInt("age");

        // This task returns a json blob (the User class).
        NodeOutput userOutput = wf.execute("fetch-user", userId);

        // You can take a Json output from a task and just copy it into a JSON_OBJ variable.
        userObject.assign(userOutput);

        // You can also use specific fields of the task output to edit a non-json variable.
        age.assign(userOutput.jsonPath("$.age"));

        // Create an expression which combines a few strings together.
        LHFormatString message = wf.format(
                "Hello there, {0}! You are {1} years old",
                userObject.jsonPath("$.title"),
                age);

        wf.execute("send-email", userObject.jsonPath("$.email"), message);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub stub = config.getBlockingStub();

        Workers taskFuncs = new Workers();
        LHTaskWorker userService = new LHTaskWorker(taskFuncs, "fetch-user", config);
        LHTaskWorker emailer = new LHTaskWorker(taskFuncs, "send-email", config);

        userService.registerTaskDef();
        emailer.registerTaskDef();

        Workflow workflow = Workflow.newWorkflow(WF_NAME, App::wfLogic);
        workflow.registerWfSpec(stub);

        Runtime.getRuntime().addShutdownHook(new Thread(userService::close));
        Runtime.getRuntime().addShutdownHook(new Thread(emailer::close));

        userService.start();
        emailer.start();

        System.out.println("Registered task defs + wfSpec '" + WF_NAME + "' and started workers.");
    }
}
