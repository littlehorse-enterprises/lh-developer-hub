# .NET Child Workflows Example

Runs `greeting-child` from `greeting-parent` and waits for its output.

```bash
dotnet run --project examples/lh-server/dotnet/07-child-workflows -- register
dotnet run --project examples/lh-server/dotnet/07-child-workflows -- workers
lhctl run greeting-parent input-name Leia
```