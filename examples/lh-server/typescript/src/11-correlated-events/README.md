# Correlated Events

This example waits for an event using a business correlation key instead of a workflow run ID.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run correlated-events
```

In another terminal, start the workflow and post the matching event:

```bash
lhctl run correlated-event-example document-id my-document-abc123
npm run correlated-events:post
```

Both commands use `my-document-abc123` as the correlation key. The event payload supplies the signer name and resumes the workflow.