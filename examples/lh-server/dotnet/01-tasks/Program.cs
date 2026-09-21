using LittleHorse.Sdk;
using LittleHorse.Sdk.Worker;
using TasksExample;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    Environment.Exit(1);
}

var config = new LHConfig();
var greeter = new Greeter();
var worker = new LHTaskWorker<Greeter>(greeter, "greet", config);

if (args[0] == "register")
{
    await worker.RegisterTaskDef();
    Console.WriteLine("Registered TaskDef greet");
}
else
{
    Console.WriteLine("Task worker started. Press Ctrl+C to stop.");
    await worker.Start();
}