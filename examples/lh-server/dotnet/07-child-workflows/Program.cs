using ChildWorkflowsExample;
using LittleHorse.Sdk;
using LittleHorse.Sdk.Worker;

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
    await GreetingWorkflows.Child().RegisterWfSpec(config.GetGrpcClientInstance());
    await GreetingWorkflows.Parent().RegisterWfSpec(config.GetGrpcClientInstance());
    Console.WriteLine("Registered child workflow example");
    return 0;
}

Console.WriteLine("Task worker started. Press Ctrl+C to stop.");
await worker.Start();
return 0;