# .NET Threads Example

Spawns a child thread that reads and mutates a variable from the parent thread.

```bash
dotnet run --project examples/lh-server/dotnet/06-threads -- register
dotnet run --project examples/lh-server/dotnet/06-threads -- workers
lhctl run threads-example
```