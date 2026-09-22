# Variables

Demonstrates required and searchable variables, task outputs, JSON paths, assignment, and formatted expressions.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./03-variables
lhctl run variables-example user-id obiwan
```