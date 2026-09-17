# External Events

This example registers the `name-posted` event, waits for it inside a workflow, and passes its string payload to a task.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run external-events
```

In another terminal, start a workflow:

```bash
lhctl run collect-underpants --wfRunId my-wf all-underpants '[]'
```
Now send external events to trigger interrupts:

```bash
lhctl postEvent my-wf underpant-collected STR anakin
lhctl postEvent my-wf underpant-collected STR yoda
```

Now send the external event that will advance the workflow:

```bash
lhctl postEvent my-wf done-collecting-underpants STR done!
```
