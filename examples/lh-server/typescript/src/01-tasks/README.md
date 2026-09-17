# Tasks

This example defines a typed `greet` function, exposes it as a LittleHorse task worker, registers its `TaskDef`, and starts polling for work.

## Run

From `examples/lh-server/typescript`:

```bash
npm install
npm run tasks
```

This example only starts the worker. Run the Workflows example to see the same task invoked by a `WfSpec`. Stop the worker with `Ctrl+C`.