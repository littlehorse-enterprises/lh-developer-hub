# Child Workflows

Registers a parent workflow that starts and waits for a child workflow.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:07-child-workflows:run
lhctl run greeting-parent input-name "Obi-Wan"
```