using LittleHorse.Sdk.Workflow.Spec;

namespace ConditionalsExample;

public static class ConditionalsWorkflow
{
    public const string WfName = "send-message";

    public static void WfLogic(WorkflowThread wf)
    {
        WfRunVariable userId = wf.DeclareStr("user-id").Required();
        WfRunVariable message = wf.DeclareStr("message").Required();
        WfRunVariable preferredContact = wf.DeclareStr("contact-method");

        preferredContact.Assign(wf.Execute("fetch-contact-method", userId));
        wf.DoIf(
            preferredContact.IsEqualTo("COMLINK"),
            ifHandler => ifHandler.Execute("send-comlink-message", userId, message),
            elseHandler => elseHandler.Execute("send-hologram", userId, message));
    }

    public static Workflow Build() => new(WfName, WfLogic);
}