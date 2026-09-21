using LittleHorse.Sdk;
using LittleHorse.Sdk.Worker;
using VariablesExample;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    return 1;
}

var config = new LHConfig();
var tasks = new MyTasks();
var emailer = new LHTaskWorker<MyTasks>(tasks, "send-email", config);
var userService = new LHTaskWorker<MyTasks>(tasks, "fetch-user", config);

if (args[0] == "register")
{
    await emailer.RegisterTaskDef();
    await userService.RegisterTaskDef();
    await VariablesWorkflow.Build().RegisterWfSpec(config.GetGrpcClientInstance());
    Console.WriteLine("Registered variables example");
    return 0;
}

Console.WriteLine("Task workers started. Press Ctrl+C to stop.");
await Task.WhenAll(emailer.Start(), userService.Start());
return 0;