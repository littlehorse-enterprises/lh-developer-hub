using LittleHorse.Sdk.Workflow.Spec;

namespace InterruptsExample;

public class UnderpantsWorkflow
{
    private WfRunVariable _allUnderpants = null!;

    public void WfLogic(WorkflowThread wf)
    {
        _allUnderpants = wf.DeclareJsonArr("all-underpants");
        wf.Execute("start-underpants-collection");
        wf.WaitForEvent("done-collecting-underpants");
        wf.Execute("profit", _allUnderpants);
        wf.RegisterInterruptHandler("underpant-collected", HandleUnderpant);
    }

    public void HandleUnderpant(WorkflowThread wf)
    {
        WfRunVariable interruptContent = wf.DeclareStr(WorkflowThread.HandlerInputVar);
        _allUnderpants.Assign(_allUnderpants.Add(interruptContent));
        wf.Execute("collect-underpant", interruptContent);
    }

    public Workflow Build() => new("collect-underpants", WfLogic);
}