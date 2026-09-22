using InterruptsExample;
using LittleHorse.Sdk;
using LittleHorse.Sdk.Common.Proto;
using LittleHorse.Sdk.Worker;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    return 1;
}

var config = new LHConfig();
var tasks = new UnderpantStealer();
var workers = new[]
{
    new LHTaskWorker<UnderpantStealer>(tasks, "start-underpants-collection", config),
    new LHTaskWorker<UnderpantStealer>(tasks, "collect-underpant", config),
    new LHTaskWorker<UnderpantStealer>(tasks, "profit", config)
};

if (args[0] == "register")
{
    await Task.WhenAll(workers.Select(worker => worker.RegisterTaskDef()));
    var client = config.GetGrpcClientInstance();
    await client.PutExternalEventDefAsync(new PutExternalEventDefRequest { Name = "underpant-collected" });
    await client.PutExternalEventDefAsync(new PutExternalEventDefRequest { Name = "done-collecting-underpants" });
    await new UnderpantsWorkflow().Build().RegisterWfSpec(client);
    Console.WriteLine("Registered interrupts example");
    return 0;
}

Console.WriteLine("Task workers started. Press Ctrl+C to stop.");
await Task.WhenAll(workers.Select(worker => worker.Start()));
return 0;