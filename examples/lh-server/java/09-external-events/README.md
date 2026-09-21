# External Events

Waits for a typed external event and passes its payload to a task.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:09-external-events:run
lhctl run greet-event
lhctl postEvent <wf-run-id> name-posted 'Obi-Wan'
```