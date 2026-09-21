# Conditionals

Routes a message to either a comlink or hologram task using `DoIfElse`.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./04-conditionals
lhctl run send-message user-id anakin message "Hello there"
```