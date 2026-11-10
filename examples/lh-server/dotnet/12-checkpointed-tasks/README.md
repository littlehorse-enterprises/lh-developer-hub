# Checkpointed Tasks

With LittleHorse running, start the worker from the repository root:

```sh
dotnet run --project examples/lh-server/dotnet/12-checkpointed-tasks -- register
dotnet run --project examples/lh-server/dotnet/12-checkpointed-tasks -- workers
```

In another terminal, run the workflow:

```sh
lhctl run checkpoint-tasks name obiwan
```

Look for these lines in the worker terminal:

```text
SIDE EFFECT: send greeting for obiwan
after checkpoint on attempt 0
after checkpoint on attempt 1
```

The first attempt intentionally fails after saving the checkpoint. The retry reuses the saved result, so `SIDE EFFECT` prints only once even though the task runs twice.

Register and run one language version at a time; all versions share the `checkpoint-tasks` WfSpec.