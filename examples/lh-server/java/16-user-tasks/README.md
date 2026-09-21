# User Tasks

Registers a user-task form, assigns it to a user, and processes the submitted fields.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:16-user-tasks:run
lhctl run favorite-player-demo user-id obiwan
```