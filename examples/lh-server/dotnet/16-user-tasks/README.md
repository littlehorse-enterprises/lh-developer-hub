# .NET User Tasks Example

Assigns a favorite-player form to a user and reports the completed values through a task worker.

```bash
dotnet run --project examples/lh-server/dotnet/16-user-tasks -- register
dotnet run --project examples/lh-server/dotnet/16-user-tasks -- workers
lhctl run favorite-player-demo user-id obiwan
```

Use the dashboard or `lhctl` to locate, assign, and complete the resulting UserTaskRun.