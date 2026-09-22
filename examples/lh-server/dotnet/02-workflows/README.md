# .NET Workflows Example

Registers the documented `greet` TaskDef and `quickstart` WfSpec.

```bash
dotnet run --project examples/lh-server/dotnet/02-workflows -- register
dotnet run --project examples/lh-server/dotnet/02-workflows -- workers
lhctl run quickstart name Obi-Wan
```