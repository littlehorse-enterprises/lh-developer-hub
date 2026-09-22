using CorrelatedEventsExample;
using LittleHorse.Sdk;
using LittleHorse.Sdk.Worker;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    return 1;
}

var config = new LHConfig();
var worker = new LHTaskWorker<DocumentProcessor>(new DocumentProcessor(), "processSignedDocument", config);

if (args[0] == "register")
{
    await worker.RegisterTaskDef();
    await CorrelatedEventWorkflow.Build().RegisterWfSpec(config.GetGrpcClientInstance());
    Console.WriteLine("Registered correlated event example");
    return 0;
}

Console.WriteLine("Task worker started. Press Ctrl+C to stop.");
await worker.Start();
return 0;