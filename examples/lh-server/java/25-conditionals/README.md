# Conditionals

This standalone Java 21 application registers a workflow with three routing branches. It first records a request, then chooses the first matching branch:

1. `amount > 100` routes to `route-large`.
2. Otherwise, `expedited == true` routes to `route-expedited`.
3. Otherwise, `route-standard` runs.

The conditions are LittleHorse expressions evaluated while the workflow runs. They are not Java `if` statements evaluated while the WfSpec is being built.

## What It Demonstrates

- Required integer and boolean workflow variables.
- `doIf(...).doElseIf(...).doElse(...)` branching.
- Explicit task retries with `withRetries(1)`.
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
./gradlew -p examples/lh-server/java/25-conditionals run
```

The application remains running to serve task work. Stop it with `Ctrl-C`; every worker is closed by the JVM shutdown hook.

Run each branch from another terminal:

```bash
# route-large
lhctl run conditionals amount 150 expedited false

# route-expedited
lhctl run conditionals amount 50 expedited true

# route-standard
lhctl run conditionals amount 50 expedited false
```
