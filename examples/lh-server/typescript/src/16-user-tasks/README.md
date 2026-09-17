# User Tasks

This example registers a typed user-task form, assigns it to a user, extracts the completed form fields, and reports the result through a task worker.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run user-tasks
```

View the User Task:
```bash
lhctl get userTaskDef report-favorite-player
```
Run the workflow: 

```bash
lhctl run favorite-player-demo user-id obiwan
```

Search for the User Task:
```bash
lhctl search userTaskRun --userTaskDefName report-favorite-player --userId obiwan
```

Complete the User Task:

```bash
lhctl execute userTaskRun <WfRunId> <GUID>
```
