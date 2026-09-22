using LittleHorse.Sdk.Workflow.Spec;

namespace ArraysExample;

public static class ArraysWorkflow
{
    public static void WfLogic(WorkflowThread wf)
    {
        WfRunVariable array = wf.DeclareArray("my-array", typeof(long));
        WfRunVariable valueToCheck = wf.DeclareInt("value-to-check").Required();
        NodeOutput produced = wf.Execute("produce-array");

        array.Assign(produced);
        array.Assign(array.Extend(4L));
        array.Assign(array.RemoveIfPresent(2L));
        array.Assign(array.RemoveIndex(1));
        wf.DoIf(
            array.DoesContain(valueToCheck),
            found => found.Execute("consume-array", array),
            notFound => notFound.Execute("process-item", valueToCheck));

        SpawnedThreads children = wf.SpawnThreadForEach(array, "process-element", child =>
        {
            WfRunVariable input = child.DeclareInt(WorkflowThread.HandlerInputVar).Required();
            child.Execute("process-item", input);
        });
        wf.WaitForThreads(children);
    }

    public static Workflow Build() => new("arrays-example", WfLogic);
}