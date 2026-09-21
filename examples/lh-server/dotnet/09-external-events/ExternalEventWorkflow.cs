using LittleHorse.Sdk.Workflow.Spec;

namespace ExternalEventsExample;

public static class ExternalEventWorkflow
{
    public const string ExternalEventDefName = "name-posted";

    public static void WfLogic(WorkflowThread wf)
    {
        WfRunVariable name = wf.DeclareStr("name");
        NodeOutput eventPayload = wf.WaitForEvent(ExternalEventDefName);
        name.Assign(eventPayload);
        wf.Execute("greet", name);
    }

    public static Workflow Build() => new("greet-event", WfLogic);
}