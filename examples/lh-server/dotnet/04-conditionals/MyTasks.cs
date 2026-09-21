using LittleHorse.Sdk.Worker;

namespace ConditionalsExample;

public class MyTasks
{
    [LHTaskMethod("fetch-contact-method")]
    public string FetchUser(string userId)
    {
        var userIds = new List<string> { "obiwan", "padme", "satine" };
        return userIds.Contains(userId) ? "COMLINK" : "HOLOGRAM";
    }

    [LHTaskMethod("send-comlink-message")]
    public string SendComlink(string userId, string message)
    {
        string result = $"sent comlink {message} to user {userId}";
        Console.WriteLine(result);
        return result;
    }

    [LHTaskMethod("send-hologram")]
    public string SendHologram(string userId, string message)
    {
        string result = $"sent hologram {message} to user {userId}";
        Console.WriteLine(result);
        return result;
    }
}