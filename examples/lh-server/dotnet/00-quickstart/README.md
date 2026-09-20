# .NET Quickstart: KYC Workflow

This is the .NET implementation used by the [LittleHorse quickstart](https://littlehorse.io/docs/getting-started/quickstart). It registers the same `quickstart` workflow and metadata as the Java, Python, and Go versions.

```bash
cd examples/lh-server/dotnet/00-quickstart
dotnet run -- register
```

Start a run, then start the workers in a second terminal:

```bash
lhctl run quickstart full-name 'Grace Hopper' email grace@example.com ssn 987654321
dotnet run -- workers
lhctl put correlatedEvent grace@example.com identity-verified BOOL true
```

See the [Java walkthrough](../../java/00-quickstart/README.md) for the workflow behavior and inspection commands shared by every language.
