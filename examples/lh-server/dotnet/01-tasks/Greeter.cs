using LittleHorse.Sdk.Worker;

namespace TasksExample;

public class Greeter
{
    [LHTaskMethod("greet")]
    public Task<string> Greeting(string name)
    {
        string result = $"Hello there, {name}!";
        Console.WriteLine(result);
        return Task.FromResult(result);
    }
}