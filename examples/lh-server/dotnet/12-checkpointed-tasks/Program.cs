using CheckpointedTasksExample;
using LittleHorse.Sdk;
using LittleHorse.Sdk.Worker;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    return 1;
}

var config = new LHConfig();
var worker = new LHTaskWorker<CheckpointTaskWorker>(new CheckpointTaskWorker(), "checkpoint-greet-dotnet", config);

if (args[0] == "register")
{
    await worker.RegisterTaskDef();
    await CheckpointedTasksWorkflow.Build().RegisterWfSpec(config.GetGrpcClientInstance());
    Console.WriteLine("Registered checkpointed tasks example");
    return 0;
}

Console.WriteLine("Run with: lhctl run checkpoint-tasks name obiwan");
await worker.Start();
return 0;