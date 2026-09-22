using LittleHorse.Sdk.Worker;

namespace ConditionalsExample;

public class MyTasks
{
    [LHTaskMethod("fetch-contact-method")]
    public Task<string> FetchContactMethod(string userId)
    {
        var userIds = new List<string> { "obiwan", "padme", "satine" };
        return Task.FromResult(userIds.Contains(userId) ? "COMLINK" : "HOLOGRAM");
    }

    [LHTaskMethod("send-comlink-message")]
    public Task<string> SendComlink(string userId, string message)
    {
        string result = $"sent comlink {message} to user {userId}";
        Console.WriteLine(result);
        return Task.FromResult(result);
    }

    [LHTaskMethod("send-hologram")]
    public Task<string> SendHologram(string userId, string message)
    {
        string result = $"sent hologram {message} to user {userId}";
        Console.WriteLine(result);
        return Task.FromResult(result);
    }
}