# .NET External Events Example

Waits for a `name-posted` event and passes its payload to the `greet` task.

```bash
dotnet run --project examples/lh-server/dotnet/09-external-events -- register
dotnet run --project examples/lh-server/dotnet/09-external-events -- workers
lhctl run greet-event --wfRunId my-run
lhctl postEvent my-run name-posted STR Leia
```