# Go Quickstart: KYC Workflow

This is the Go implementation used by the [LittleHorse quickstart](https://littlehorse.io/docs/getting-started/quickstart). It registers the same `quickstart` workflow and metadata as the Java, Python, and .NET versions.

```bash
cd examples/lh-server/go/00-quickstart
go run . register
```

Start a run, then start the workers in a second terminal:

```bash
lhctl run quickstart full-name 'Grace Hopper' email grace@example.com ssn 987654321
go run . workers
lhctl put correlatedEvent grace@example.com identity-verified BOOL true
```

See the [Java walkthrough](../../java/00-quickstart/README.md) for the workflow behavior and inspection commands shared by every language.
