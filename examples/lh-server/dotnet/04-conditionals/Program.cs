using ConditionalsExample;
using LittleHorse.Sdk;
using LittleHorse.Sdk.Worker;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    return 1;
}

var config = new LHConfig();
var tasks = new MyTasks();
var workers = new[]
{
    new LHTaskWorker<MyTasks>(tasks, "fetch-contact-method", config),
    new LHTaskWorker<MyTasks>(tasks, "send-comlink-message", config),
    new LHTaskWorker<MyTasks>(tasks, "send-hologram", config)
};

if (args[0] == "register")
{
    await Task.WhenAll(workers.Select(worker => worker.RegisterTaskDef()));
    await ConditionalsWorkflow.Build().RegisterWfSpec(config.GetGrpcClientInstance());
    Console.WriteLine("Registered conditionals example");
    return 0;
}

Console.WriteLine("Task workers started. Press Ctrl+C to stop.");
await Task.WhenAll(workers.Select(worker => worker.Start()));
return 0;