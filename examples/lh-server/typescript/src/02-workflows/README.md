# Workflows

This example creates the `quickstart` workflow, declares a required and searchable string variable, invokes the `greet` task, and starts its worker.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run workflows
```

In another terminal, run the workflow:

```bash
lhctl run quickstart name Obi-Wan
```

The worker prints `Hello Obi-Wan!`. Keep the application running while starting workflow runs, then stop it with `Ctrl+C`.