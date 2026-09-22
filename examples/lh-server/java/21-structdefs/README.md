# StructDefs

Registers the `car` StructDef and uses it as a workflow input and task parameter.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
./gradlew :examples:lh-server:java:21-structdefs:run
./gradlew :examples:lh-server:java:21-structdefs:run --args=run
```

The second command creates `new Car("Pontiac", "Aztek", 2005)`, converts it with `LHLibUtil.objToVarVal()`, and runs the `quickstart` WfSpec exactly as shown on the website.
