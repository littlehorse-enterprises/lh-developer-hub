using LittleHorse.Sdk.Worker;

namespace ExternalEventsExample;

public class Greeter
{
    [LHTaskMethod("greet")]
    public string Greet(string name)
    {
        string result = $"Hello, {name}!";
        Console.WriteLine(result);
        return result;
    }
}