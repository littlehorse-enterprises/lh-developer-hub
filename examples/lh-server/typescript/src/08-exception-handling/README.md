# Exception Handling

This example demonstrates exception handlers, custom exception names, and workflow recovery behavior.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run exceptions
```

In another terminal, run the workflow repeatedly to observe its success and failure paths:

```bash
lhctl run exception-example price 50.0
```

Stop the application with `Ctrl+C` when finished.