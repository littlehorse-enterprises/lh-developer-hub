# Workflows

Registers the `quickstart` workflow and its `greet` task worker.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:02-workflows:run
lhctl run quickstart name Obi-Wan
```