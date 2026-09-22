# External Events

This example registers the `name-posted` event, waits for it inside a workflow, and passes its string payload to a task.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run external-events
```

In another terminal, start a workflow:

```bash
lhctl run greet-event --wfRunId my-wf
```

Send the string payload that the workflow passes to the `greet` task:

```bash
lhctl postEvent my-wf name-posted STR Obi-Wan
```
