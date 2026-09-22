# External Events

Waits for a typed `name-posted` ExternalEvent before running the greeting task.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./09-external-events
lhctl run greet-event --wfRunId my-run
lhctl postEvent my-run name-posted STR Obi-Wan
```