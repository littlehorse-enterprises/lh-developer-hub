# User Tasks

This example registers a typed user-task form, assigns it to a user, extracts the completed form fields, and reports the result through a task worker.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run user-tasks
```

In another terminal, create a workflow run and complete its user task automatically:

```bash
npm run user-tasks:run
```

The runner acts like a minimal UI client: it finds the user task assigned to `obiwan`, submits the form fields, and waits for workflow completion.