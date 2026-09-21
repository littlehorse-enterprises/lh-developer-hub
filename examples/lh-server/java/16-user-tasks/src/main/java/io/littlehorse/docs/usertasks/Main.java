package io.littlehorse.docs.usertasks;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.littlehorse.sdk.wfsdk.UserTaskOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.util.List;

public class Main {

    private static final String USER_TASKDEF_NAME = "report-favorite-player";

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable userId = wf.declareStr("user-id").searchable().required();
        WfRunVariable favoriteTeam = wf.declareStr("favorite-team");
        WfRunVariable favoriteNumber = wf.declareInt("favorite-player-number");
        UserTaskOutput formResult = wf.assignUserTask(USER_TASKDEF_NAME, userId, null);

        favoriteTeam.assign(formResult.jsonPath("$.favoriteTeam"));
        favoriteNumber.assign(formResult.jsonPath("$.favoritePlayerNumber"));
        wf.execute("report-favorite-player", userId, favoriteTeam, favoriteNumber);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        FavoritePlayerTasks tasks = new FavoritePlayerTasks();
        LHTaskWorker worker = new LHTaskWorker(tasks, "report-favorite-player", config);
        config.getBlockingStub().putUserTaskDef(
                new UserTaskSchema(new FavoritePlayerForm(), USER_TASKDEF_NAME).compile());
        Workflow workflow = Workflow.newWorkflow("favorite-player-demo", Main::wfLogic);

        System.out.println("Run with: lhctl run favorite-player-demo user-id obiwan");
        ExampleSupport.start(config, List.of(worker), workflow);
    }
}