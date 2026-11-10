using LittleHorse.Sdk.Worker;

namespace CheckpointedTasksExample;

public class CheckpointTaskWorker
{
    [LHTaskMethod("checkpoint-greet-dotnet")]
    public Task<string> Greeting(string name, LHWorkerContext context)
    {
        int attemptNumber = context.AttemptNumber;
        Console.WriteLine(
            $"metadata: wfRun={context.WfRunId.Id} taskRun={context.TaskRunId.TaskGuid} attempt={attemptNumber}");

        string? sideEffectReceipt = context.ExecuteAndCheckpoint<string>(checkpointContext =>
        {
            checkpointContext.Log("sending the greeting side effect");
            Console.WriteLine("SIDE EFFECT: send greeting for " + name);
            return "receipt-for-" + name;
        });

        Console.WriteLine("after checkpoint on attempt " + attemptNumber);
        if (attemptNumber == 0)
        {
            throw new InvalidOperationException("Intentional post-checkpoint failure on the first attempt.");
        }

        return Task.FromResult(sideEffectReceipt + "; completed on attempt " + attemptNumber);
    }
}