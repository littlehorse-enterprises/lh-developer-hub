using LittleHorse.Sdk.Workflow.Spec;

namespace VariablesExample;

public static class VariablesWorkflow
{
    public const string WfName = "variables-example";

    public static void WfLogic(WorkflowThread wf)
    {
        WfRunVariable userId = wf.DeclareStr("user-id").Required().Searchable();
        WfRunVariable userObject = wf.DeclareJsonObj("user-obj");
        WfRunVariable age = wf.DeclareInt("age");
        NodeOutput userOutput = wf.Execute("fetch-user", userId);

        userObject.Assign(userOutput);
        age.Assign(userOutput.WithJsonPath("$.Age"));

        LHFormatString message = wf.Format(
            "Hello there, {0}! You are {1} years old",
            userObject.WithJsonPath("$.Title"),
            age);
        wf.Execute("send-email", userObject.WithJsonPath("$.Email"), message);
    }

    public static Workflow Build() => new(WfName, WfLogic);
}