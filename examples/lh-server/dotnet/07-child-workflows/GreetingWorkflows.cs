using LittleHorse.Sdk.Workflow.Spec;

namespace ChildWorkflowsExample;

public static class GreetingWorkflows
{
    public static void ChildLogic(WorkflowThread wf)
    {
        WfRunVariable name = wf.DeclareStr("name").Required();
        wf.Complete(wf.Execute("greet", name));
    }

    public static void ParentLogic(WorkflowThread wf)
    {
        WfRunVariable inputName = wf.DeclareStr("input-name").Required();
        WfRunVariable childOutput = wf.DeclareStr("child-output");
        SpawnedChildWf child = wf.RunWf(
            "greeting-child",
            new Dictionary<string, object> { { "name", inputName } });

        wf.Execute("greet", "hi from parent");
        childOutput.Assign(wf.WaitForChildWf(child));
    }

    public static Workflow Child() => new("greeting-child", ChildLogic);
    public static Workflow Parent() => new("greeting-parent", ParentLogic);
}