# Maps

Runs a `produce-map` task and assigns its native `Map<String, Long>` output to the workflow's `my-map` variable.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:23-maps:run
lhctl run maps-example
```