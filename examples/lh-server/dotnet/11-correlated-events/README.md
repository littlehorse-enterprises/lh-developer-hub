# .NET Correlated Events Example

Routes a `document-signed` event to the workflow waiting on its document ID.

```bash
dotnet run --project examples/lh-server/dotnet/11-correlated-events -- register
dotnet run --project examples/lh-server/dotnet/11-correlated-events -- workers
lhctl run correlated-event-example document-id my-document-abc123
lhctl put correlatedEvent my-document-abc123 document-signed STR Leia
```