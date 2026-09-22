# Python User Tasks Example

Registers the documented favorite-player UserTaskDef, WfSpec, and worker.

From the repository root, complete the [shared Python setup](../README.md), then run:

```bash
python examples/lh-server/python/16-user-tasks/main.py register
python examples/lh-server/python/16-user-tasks/main.py workers
lhctl run favorite-player-demo user-id obiwan
lhctl search userTaskRun --userTaskDefName report-favorite-player --userId obiwan
lhctl execute userTaskRun <WfRunId> <Guid>
```

Use the search result's `WfRunId` and `Guid` in the execute command to complete the UserTaskRun.