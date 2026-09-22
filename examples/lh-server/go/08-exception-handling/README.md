# Exception Handling

Handles a business exception separately from technical task failures.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./08-exception-handling
lhctl run exception-example price 50.0
```