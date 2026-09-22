using LittleHorse.Sdk.Workflow.Spec;

namespace UserTasksExample;

public static class FavoritePlayerWorkflow
{
    public const string UserTaskDefName = "report-favorite-player";

    public static void WfLogic(WorkflowThread wf)
    {
        WfRunVariable userId = wf.DeclareStr("user-id").Searchable().Required();
        WfRunVariable favoriteTeam = wf.DeclareStr("favorite-team");
        WfRunVariable favoriteNumber = wf.DeclareInt("favorite-player-number");
        UserTaskOutput formResult = wf.AssignUserTask(UserTaskDefName, userId, null);

        favoriteTeam.Assign(formResult.WithJsonPath("$.FavoriteTeam"));
        favoriteNumber.Assign(formResult.WithJsonPath("$.FavoritePlayerNumber"));
        wf.Execute("report-favorite-player", userId, favoriteTeam, favoriteNumber);
    }

    public static Workflow Build() => new("favorite-player-demo", WfLogic);
}