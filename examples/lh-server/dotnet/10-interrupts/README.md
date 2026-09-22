# .NET Interrupts Example

Collects event payloads in interrupt-handler threads until the main thread receives a completion event.

```bash
dotnet run --project examples/lh-server/dotnet/10-interrupts -- register
dotnet run --project examples/lh-server/dotnet/10-interrupts -- workers
lhctl run collect-underpants --wfRunId my-run all-underpants '[]'
lhctl postEvent my-run underpant-collected STR obiwan
lhctl postEvent my-run done-collecting-underpants
```