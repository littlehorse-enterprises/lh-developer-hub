# Variables

Demonstrates required, searchable, JSON object, and integer variables with JSON-path access.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:03-variables:run
lhctl run variables-example user-id obiwan
```