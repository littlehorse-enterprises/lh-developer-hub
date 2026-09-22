# Interrupts

Collects event payloads in interrupt-handler threads until the main thread receives a completion event.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./10-interrupts
lhctl run collect-underpants --wfRunId my-run all-underpants '[]'
lhctl postEvent my-run underpant-collected STR Stan
lhctl postEvent my-run done-collecting-underpants
```