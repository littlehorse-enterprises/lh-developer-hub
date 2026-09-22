# User Tasks

Registers a user-task form, assigns it to a user, and reports the completed form values with a TaskDef.

## Prerequisites

A compatible LittleHorse Server must be running and reachable through `LHC_*` environment variables or SDK defaults. See the shared [server setup](../../README.md#littlehorse-server-version).

From the repository root:

```bash
go -C examples/lh-server/go run ./16-user-tasks
lhctl run favorite-player-demo user-id obiwan
```

The completed field values remain available on the UserTaskRun. LittleHorse Go SDK 1.3.0 does not preserve typed user-task field selectors when compiling against current servers, so the downstream task receives the assignee ID rather than individual form fields.