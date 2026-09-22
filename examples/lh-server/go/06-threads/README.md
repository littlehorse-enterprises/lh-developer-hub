# Threads

Demonstrates child threads, shared variables, sleeps, and waiting for child completion.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./06-threads
lhctl run threads-example
```