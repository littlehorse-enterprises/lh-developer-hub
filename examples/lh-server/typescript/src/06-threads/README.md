# Threads

This example demonstrates spawning child threads, passing thread inputs, waiting for child completion, and handling work concurrently inside a workflow run.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run threads
```

In another terminal:

```bash
lhctl run threads-example
```

Keep the workers running until the workflow completes, then stop them with `Ctrl+C`.