using LittleHorse.Sdk.Worker;

namespace WorkflowsExample;

public class Greeter
{
    [LHTaskMethod("greet")]
    public Task<string> Greeting(string name)
    {
        string result = $"Hello {name}!";
        Console.WriteLine(result);
        return Task.FromResult(result);
    }
}