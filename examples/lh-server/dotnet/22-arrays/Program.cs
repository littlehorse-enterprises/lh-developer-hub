using ArraysExample;
using LittleHorse.Sdk;
using LittleHorse.Sdk.Worker;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    return 1;
}

var config = new LHConfig();
var tasks = new ArrayWorker();
var producer = new LHTaskWorker<ArrayWorker>(tasks, "produce-array", config);
var consumer = new LHTaskWorker<ArrayWorker>(tasks, "consume-array", config);

if (args[0] == "register")
{
    await Task.WhenAll(producer.RegisterTaskDef(), consumer.RegisterTaskDef());
    await ArraysWorkflow.Build().RegisterWfSpec(config.GetGrpcClientInstance());
    Console.WriteLine("Registered arrays example");
    return 0;
}

Console.WriteLine("Task workers started. Press Ctrl+C to stop.");
await Task.WhenAll(producer.Start(), consumer.Start());
return 0;