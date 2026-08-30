# Task Failures

This standalone Java 21 application demonstrates the difference between technical task errors and named business exceptions. The workflow accepts a `scenario` input and always proceeds to `finish-operation` after the relevant failure handler completes.

## Scenarios

- `success`: `perform-operation` returns normally on attempt `0`.
- `retryable-runtime`: `perform-operation` throws a `RuntimeException` on every attempt. The task has one retry, so LittleHorse invokes it on attempts `0` and `1`; after retries are exhausted, `handleError` runs `recover-technical-error`.
- `business-exception`: `perform-operation` throws an `LHTaskException` with a name, human-readable message, and string content. `handleException` matches the exception name, receives the content through the handler's `INPUT` variable, and passes the name and message to `recover-business-exception`.

Business exceptions are named workflow outcomes, not technical errors. They are not retried, even though the operation node has a retry policy. Use `RuntimeException` for transient technical failures that should be retried; use `LHTaskException` when the workflow should handle a business outcome.

## What It Demonstrates

- `WorkerContext.getAttemptNumber()` in the operation, recovery, and finish tasks.
- `handleError(...)` for a technical failure after task retries are exhausted.
- `handleException(..., exceptionName, ...)` for a named business exception.
- `LHTaskException` content, name, and message.
- One-hour workflow-run retention with `WorkflowRetentionPolicy`.
- `new LHConfig()` for standard `LHC_*` environment configuration.
- Task registration before WfSpec registration, worker startup, and graceful shutdown hooks.

## Prerequisites

- Java 21+
- A reachable LittleHorse server
- `lhctl` configured for the same server

## Run

From this directory:

```bash
../../../../gradlew run
```

From the repository root:

```bash
./gradlew -p examples/lh-server/java/35-task-failures run
```

The application remains running to serve task work. Stop it with `Ctrl-C`; the shutdown hook closes every worker.

Run the three deterministic scenarios from another terminal:

```bash
lhctl run task-failures scenario success
lhctl run task-failures scenario retryable-runtime
lhctl run task-failures scenario business-exception
```

Inspect the WfRun and its node/task runs with the usual `lhctl get wfRun`, `lhctl list nodeRun`, and `lhctl get taskRun` commands. The task worker output shows each attempt and the business exception fields.
