using LittleHorse.Sdk.Worker;

namespace ExternalEventsExample;

public class Greeter
{
    [LHTaskMethod("greet")]
    public Task<string> Greet(string name)
    {
        string result = $"Hello, {name}!";
        Console.WriteLine(result);
        return Task.FromResult(result);
    }
}