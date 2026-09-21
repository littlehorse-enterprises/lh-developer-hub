# Tasks

This example registers and starts the `greet` task worker from the Tasks concept documentation.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

## Run

From the repository root, start the worker:

```bash
./gradlew :examples:lh-server:java:01-tasks:run
```

Keep the worker running until you finish the Workflows example, then stop it with `Ctrl+C`.