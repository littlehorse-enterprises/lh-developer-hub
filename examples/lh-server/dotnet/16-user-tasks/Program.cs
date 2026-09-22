using LittleHorse.Sdk;
using LittleHorse.Sdk.UserTask;
using LittleHorse.Sdk.Worker;
using UserTasksExample;

if (args.Length != 1 || (args[0] != "register" && args[0] != "workers"))
{
    Console.Error.WriteLine("Please provide one argument: either 'register' or 'workers'");
    return 1;
}

var config = new LHConfig();
var worker = new LHTaskWorker<PlayerReporter>(new PlayerReporter(), "report-favorite-player", config);

if (args[0] == "register")
{
    await worker.RegisterTaskDef();
    var schema = new UserTaskSchema(new FavoritePlayerForm(), FavoritePlayerWorkflow.UserTaskDefName);
    await config.GetGrpcClientInstance().PutUserTaskDefAsync(schema.Compile());
    await FavoritePlayerWorkflow.Build().RegisterWfSpec(config.GetGrpcClientInstance());
    Console.WriteLine("Registered user task example");
    return 0;
}

Console.WriteLine("Task worker started. Press Ctrl+C to stop.");
await worker.Start();
return 0;