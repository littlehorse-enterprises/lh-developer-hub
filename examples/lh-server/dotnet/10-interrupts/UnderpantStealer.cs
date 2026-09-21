using LittleHorse.Sdk.Worker;

namespace InterruptsExample;

public class UnderpantStealer
{
    [LHTaskMethod("start-underpants-collection")]
    public void StartCollection() => Console.WriteLine("Starting collection of underpants!");

    [LHTaskMethod("collect-underpant")]
    public string ShipItem(string underpantOwner)
    {
        string result = $"Successfully collected underpant from {underpantOwner}";
        Console.WriteLine(result);
        return result;
    }

    [LHTaskMethod("profit")]
    public string Profit(List<string> underpants)
    {
        string result = $"Collected {underpants.Count} underpants!";
        Console.WriteLine(result);
        return result;
    }
}