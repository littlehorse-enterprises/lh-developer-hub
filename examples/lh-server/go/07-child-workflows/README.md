# Child Workflows

Registers a parent workflow that starts and waits for a child workflow.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./07-child-workflows
lhctl run greeting-parent input-name "Obi-Wan"
```