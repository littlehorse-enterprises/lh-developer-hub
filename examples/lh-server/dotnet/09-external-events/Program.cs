using ExternalEventsExample;
using LittleHorse.Sdk;
using LittleHorse.Sdk.Common.Proto;
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
    var client = config.GetGrpcClientInstance();
    await client.PutExternalEventDefAsync(new PutExternalEventDefRequest
    {
        Name = ExternalEventWorkflow.ExternalEventDefName
    });
    await ExternalEventWorkflow.Build().RegisterWfSpec(client);
    Console.WriteLine("Registered external event example");
    return 0;
}

Console.WriteLine("Task worker started. Press Ctrl+C to stop.");
await worker.Start();
return 0;