using LittleHorse.Sdk.Worker;

namespace ThreadsExample;

public class MyWorker
{
    [LHTaskMethod("my-task")]
    public string MyTask(string input, LHWorkerContext context)
    {
        int threadRunNumber = context.NodeRunId!.ThreadRunNumber;
        string threadName = threadRunNumber == 0 ? "parent" : "child";
        string result = $"Hello from the {threadName} thread: {input}";
        Console.WriteLine(result);
        return result;
    }
}