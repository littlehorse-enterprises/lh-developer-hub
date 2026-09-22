# Correlated Events

Waits for a document-signing event using the document ID as its correlation key.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./11-correlated-events
lhctl run correlated-event-example document-id my-document-abc123
```