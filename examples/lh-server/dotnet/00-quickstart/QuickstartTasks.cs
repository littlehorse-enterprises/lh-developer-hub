using LittleHorse.Sdk.Worker;

namespace Quickstart;

public class QuickstartTasks
{
    [LHTaskMethod(QuickstartWorkflow.VerifyIdentityTask)]
    public string VerifyIdentity(string fullName, string email, int ssn)
    {
        return $"Verification request accepted for {fullName} at {email}";
    }

    [LHTaskMethod(QuickstartWorkflow.NotifyVerifiedTask)]
    public string NotifyCustomerVerified(string fullName, string email)
    {
        return $"Notified {fullName} at {email} that their identity was verified";
    }

    [LHTaskMethod(QuickstartWorkflow.NotifyNotVerifiedTask)]
    public string NotifyCustomerNotVerified(string fullName, string email)
    {
        return $"Notified {fullName} at {email} that their identity was not verified";
    }
}
