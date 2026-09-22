# Threads

Spawns a child thread, shares a parent variable, and waits for the child to finish.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:06-threads:run
lhctl run threads-example
```