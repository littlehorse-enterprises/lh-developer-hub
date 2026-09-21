# Workflows

Registers the `quickstart` WfSpec and starts its `greet` task worker.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./02-workflows
lhctl run quickstart name "Obi-Wan"
```