# Timestamps

This standalone Java 21 application registers a workflow that accepts a LittleHorse `TIMESTAMP` variable, passes it through a task, and uses the task output in a second task.

## What It Demonstrates

- `wf.declareTimestamp("event-time")` creates a required timestamp workflow variable.
- `Instant` is a supported Java representation for a LittleHorse timestamp.
- `withRetries(2)` allows the formatting task two retries after a failed attempt.
- `withRetentionPolicy(...)` retains completed or failed workflow runs for one hour.
- `new LHConfig()` reads the standard LittleHorse `LHC_*` environment configuration.
- Task definitions are registered before the WfSpec, workers are started after registration, and a JVM shutdown hook closes them.

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
./gradlew -p examples/lh-server/java/20-timestamps run
```

The process stays alive while the workers poll for tasks. Stop it with `Ctrl-C`; the shutdown hook closes both workers.

In another terminal, start a workflow with an ISO-8601 timestamp:

```bash
lhctl run timestamps event-time 2026-01-01T12:00:00Z
```

The `print-timestamp` worker prints the formatted timestamp. The retention policy is part of the registered WfSpec and is applied after the run terminates.
