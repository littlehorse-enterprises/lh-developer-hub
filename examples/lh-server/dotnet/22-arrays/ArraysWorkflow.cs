using LittleHorse.Sdk.Workflow.Spec;

namespace ArraysExample;

public static class ArraysWorkflow
{
    public static void WfLogic(WorkflowThread wf)
    {
        WfRunVariable array = wf.DeclareArray("my-numbers", typeof(long));
        NodeOutput produced = wf.Execute("produce-array");

        array.Assign(produced);
        array.Assign(array.Extend(4L));
        array.Assign(array.RemoveIfPresent(2L));
        array.Assign(array.RemoveIndex(1));
        wf.Execute("consume-array", array);
    }

    public static Workflow Build() => new("arrays-example", WfLogic);
}