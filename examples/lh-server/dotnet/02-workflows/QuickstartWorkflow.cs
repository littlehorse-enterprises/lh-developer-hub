using LittleHorse.Sdk.Workflow.Spec;

namespace WorkflowsExample;

public static class QuickstartWorkflow
{
    public const string WfName = "quickstart";

    public static Workflow Build()
    {
        return new Workflow(WfName, wf =>
        {
            WfRunVariable name = wf.DeclareStr("name").Searchable().Required();
            wf.Execute("greet", name);
        });
    }
}