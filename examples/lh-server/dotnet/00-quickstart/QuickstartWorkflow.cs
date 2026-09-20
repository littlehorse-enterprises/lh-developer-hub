using LittleHorse.Sdk.Common.Proto;
using LittleHorse.Sdk.Workflow.Spec;

namespace Quickstart;

public static class QuickstartWorkflow
{
    public const string WfSpecName = "quickstart";
    public const string IdentityVerifiedEvent = "identity-verified";
    public const string VerifyIdentityTask = "verify-identity";
    public const string NotifyVerifiedTask = "notify-customer-verified";
    public const string NotifyNotVerifiedTask = "notify-customer-not-verified";

    public static Workflow GetWorkflow()
    {
        void EntryPoint(WorkflowThread wf)
        {
            WfRunVariable fullName = wf.DeclareStr("full-name").Searchable().Required();
            WfRunVariable email = wf.DeclareStr("email").Searchable().Required();
            WfRunVariable ssn = wf.DeclareInt("ssn").Masked().Required();
            WfRunVariable identityVerified = wf.DeclareBool("identity-verified").Searchable();

            wf.Execute(VerifyIdentityTask, fullName, email, ssn).WithRetries(3);
            ExternalEventNodeOutput verification = wf.WaitForEvent(IdentityVerifiedEvent)
                .WithTimeout(300)
                .WithCorrelationId(email)
                .RegisteredAs(typeof(bool));

            wf.HandleError(verification, LHErrorType.Timeout, handler =>
            {
                handler.Execute(NotifyNotVerifiedTask, fullName, email);
                handler.Fail("customer-not-verified", "Unable to verify customer identity in time.");
            });

            identityVerified.Assign(verification);
            wf.DoIf(
                identityVerified.IsEqualTo(true),
                yes => yes.Execute(NotifyVerifiedTask, fullName, email),
                no => no.Execute(NotifyNotVerifiedTask, fullName, email));
        }

        return new Workflow(WfSpecName, EntryPoint);
    }
}
