# Conditionals

This example demonstrates `doIfElse` and comparison expressions by selecting how a message is delivered.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run conditionals
```

In another terminal:

```bash
lhctl run send-message user-id anakin message "Hello there"
```

The selected branch invokes its corresponding task worker. Stop the application with `Ctrl+C`.