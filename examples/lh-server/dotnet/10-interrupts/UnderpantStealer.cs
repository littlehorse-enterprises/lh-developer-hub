using LittleHorse.Sdk.Worker;

namespace InterruptsExample;

public class UnderpantStealer
{
    [LHTaskMethod("start-underpants-collection")]
    public Task StartCollection()
    {
        Console.WriteLine("Starting collection of underpants!");
        return Task.CompletedTask;
    }

    [LHTaskMethod("collect-underpant")]
    public Task<string> CollectUnderpant(string underpantOwner)
    {
        string result = $"Successfully collected underpant from {underpantOwner}";
        Console.WriteLine(result);
        return Task.FromResult(result);
    }

    [LHTaskMethod("profit")]
    public Task<string> Profit(List<string> underpants)
    {
        string result = $"Collected {underpants.Count} underpants!";
        Console.WriteLine(result);
        return Task.FromResult(result);
    }
}