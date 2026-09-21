using LittleHorse.Sdk.Workflow.Spec;

namespace ThreadsExample;

public class ThreadsWorkflow
{
    private WfRunVariable _parentVariable = null!;

    public void EntrypointThreadLogic(WorkflowThread wf)
    {
        _parentVariable = wf.DeclareStr("parent-var")
            .WithDefault("This is the parent variable's initial value");
        wf.Execute("my-task", _parentVariable);

        SpawnedThread child = wf.SpawnThread(
            "child",
            ChildThreadLogic,
            new Dictionary<string, object>
            {
                { "child-input", "This is the input to the child thread" }
            });

        wf.SleepSeconds(25);
        wf.Execute("my-task", _parentVariable);
        wf.WaitForThreads(SpawnedThreads.Of(child));
    }

    public void ChildThreadLogic(WorkflowThread wf)
    {
        WfRunVariable childInput = wf.DeclareStr("child-input").Required();
        wf.Execute("my-task", childInput);
        wf.Execute("my-task", _parentVariable);
        _parentVariable.Assign("This is the value of the parent variable set by the child.");
        wf.SleepSeconds(45);
    }

    public Workflow Build() => new("threads-example", EntrypointThreadLogic);
}