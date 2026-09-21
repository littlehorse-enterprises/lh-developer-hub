using LittleHorse.Sdk.Common.Proto;
using LittleHorse.Sdk.Workflow.Spec;

namespace CorrelatedEventsExample;

public static class CorrelatedEventWorkflow
{
    public static void WfLogic(WorkflowThread wf)
    {
        WfRunVariable documentId = wf.DeclareStr("document-id");
        NodeOutput signerName = wf.WaitForEvent("document-signed")
            .WithCorrelationId(documentId)
            .WithCorrelatedEventConfig(new CorrelatedEventConfig
            {
                DeleteAfterFirstCorrelation = true
            })
            .RegisteredAs(typeof(string));

        wf.Execute("processSignedDocument", documentId, signerName);
    }

    public static Workflow Build() => new("correlated-event-example", WfLogic);
}