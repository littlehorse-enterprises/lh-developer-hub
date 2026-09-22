# Interrupts

Collects interrupt payloads while the main workflow waits for a completion event.

The `underpant-collected` event is registered as a `STR` by `.withEventType(String.class)`. The payload-free `done-collecting-underpants` event is registered explicitly before the `WfSpec` is deployed.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:10-interrupts:run
lhctl run collect-underpants --wfRunId my-wf all-underpants '[]'
lhctl postEvent my-wf underpant-collected STR Stan
lhctl postEvent my-wf done-collecting-underpants
```