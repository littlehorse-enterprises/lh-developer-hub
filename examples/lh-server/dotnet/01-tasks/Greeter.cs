using LittleHorse.Sdk.Worker;

namespace TasksExample;

public class Greeter
{
    [LHTaskMethod("greet")]
    public string Greeting(string name)
    {
        string result = $"Hello there, {name}!";
        Console.WriteLine(result);
        return result;
    }
}