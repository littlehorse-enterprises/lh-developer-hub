# .NET Arrays Example

Produces, mutates, inspects, consumes, and spawns a child thread for each element of a strongly typed native LittleHorse array.

```bash
dotnet run --project examples/lh-server/dotnet/22-arrays -- register
dotnet run --project examples/lh-server/dotnet/22-arrays -- workers
lhctl run arrays-example value-to-check 3
```

The .NET SDK 1.3.0 does not expose the Java SDK's array `Size()` or indexed-read expressions. This example reports the element count in the `consume-array` task instead; containment and per-element child threads remain part of the workflow.