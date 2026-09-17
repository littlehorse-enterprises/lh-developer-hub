# Child Workflows

This example registers parent and child `WfSpec`s, passes an input into the child workflow, and uses the child result in the parent.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run child-workflows
```

In another terminal:

```bash
lhctl run greeting-parent input-name "Obi-Wan"
```

Stop the application with `Ctrl+C` after the parent and child runs complete.