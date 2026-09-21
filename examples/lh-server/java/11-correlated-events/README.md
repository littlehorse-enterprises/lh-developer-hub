# Correlated Events

Waits for a document event correlated by document ID.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:11-correlated-events:run
lhctl run correlated-event-example document-id my-document-abc123
```

Post a `document-signed` correlated event with key `my-document-abc123` and a string payload.