# Arrays

Uses native typed arrays in workflow variables, task input/output, mutations, and child threads.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:22-arrays:run
lhctl run arrays-example my-array '[1,2,3]' value-to-check 3
```