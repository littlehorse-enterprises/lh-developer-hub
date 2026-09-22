using LittleHorse.Sdk;
using LittleHorse.Sdk.Worker;
using WorkflowsExample;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    return 1;
}

var config = new LHConfig();
var worker = new LHTaskWorker<Greeter>(new Greeter(), "greet", config);

if (args[0] == "register")
{
    await worker.RegisterTaskDef();
    await QuickstartWorkflow.Build().RegisterWfSpec(config.GetGrpcClientInstance());
    Console.WriteLine("Registered TaskDef greet and WfSpec quickstart");
    return 0;
}

Console.WriteLine("Task worker started. Press Ctrl+C to stop.");
await worker.Start();
return 0;