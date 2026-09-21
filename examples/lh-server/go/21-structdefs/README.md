# StructDefs

Registers a `car` StructDef and uses it as a typed workflow input and task parameter.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./21-structdefs
lhctl run structdef-example input-car '{"make":"Incom","model":"T-65 X-wing","year":1977}'
```