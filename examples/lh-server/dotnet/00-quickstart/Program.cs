using LittleHorse.Sdk;
using LittleHorse.Sdk.Worker;
using Quickstart;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    Environment.Exit(1);
}

var config = new LHConfig();
var tasks = new QuickstartTasks();
var workers = new[]
{
    new LHTaskWorker<QuickstartTasks>(tasks, QuickstartWorkflow.VerifyIdentityTask, config),
    new LHTaskWorker<QuickstartTasks>(tasks, QuickstartWorkflow.NotifyVerifiedTask, config),
    new LHTaskWorker<QuickstartTasks>(tasks, QuickstartWorkflow.NotifyNotVerifiedTask, config),
};

if (args[0] == "register")
{
    await Task.WhenAll(workers.Select(worker => worker.RegisterTaskDef()));
    await QuickstartWorkflow.GetWorkflow().RegisterWfSpec(config.GetGrpcClientInstance());
    Console.WriteLine($"Registered TaskDefs, ExternalEventDef, and WfSpec {QuickstartWorkflow.WfSpecName}");
}
else
{
    Console.WriteLine("Task workers started. Press Ctrl+C to stop.");
    await Task.WhenAll(workers.Select(worker => worker.Start()));
}
