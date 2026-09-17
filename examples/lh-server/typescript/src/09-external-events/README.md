# External Events

This example registers the `name-posted` event, waits for it inside a workflow, and passes its string payload to a task.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run external-events
```

In another terminal, start a workflow and copy the returned workflow run ID:

```bash
lhctl run greet-event
lhctl postEvent <wfRunId> name-posted
```

The posting script sends `Obi-Wan Kenobi` as the event payload. The waiting workflow then resumes and invokes `greet`.