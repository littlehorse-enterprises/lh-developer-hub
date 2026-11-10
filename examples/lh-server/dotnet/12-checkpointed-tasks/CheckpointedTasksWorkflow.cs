using LittleHorse.Sdk.Workflow.Spec;

namespace CheckpointedTasksExample;

public static class CheckpointedTasksWorkflow
{
    public static void WfLogic(WorkflowThread wf)
    {
        WfRunVariable name = wf.DeclareStr("name").Required();
        wf.Execute("checkpoint-greet-dotnet", name).WithRetries(2);
    }

    public static Workflow Build() => new("checkpoint-tasks", WfLogic);
}