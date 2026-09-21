# Maps

Declares native typed maps and demonstrates merge and key-removal mutations.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:23-maps:run
lhctl run maps-example scores '{"alice":10}' scores-to-merge '{"bob":20}' key-to-remove alice
```