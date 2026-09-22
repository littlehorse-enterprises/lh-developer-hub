# Java Quickstart: KYC Workflow

This example powers the Java tab in the [LittleHorse quickstart](https://littlehorse.io/docs/getting-started/quickstart). You will learn how to:

- Register task definitions, a typed correlated external event, and a `WfSpec`.
- Use required, searchable, and masked workflow variables.
- Retry a technical task failure with `.withRetries(3)`.
- Wait for a typed boolean event correlated by email.
- Handle an event timeout and use `WorkerContext` inside a task worker.
- Register metadata separately from the long-lived task workers.

## Workflow

```mermaid
flowchart LR
    A[Start quickstart] --> B[verify-identity, retry up to 3 times]
    B --> C[Wait for typed identity-verified event]
    C -->|true| D[notify-customer-verified]
    C -->|false| E[notify-customer-not-verified]
    C -->|timeout| F[Notify and fail customer-not-verified]
```

`full-name` and `email` are required and searchable. `ssn` is required and masked. The event content is a boolean, and its correlation key is the email string.

## Prerequisites

- Java 21 or newer.
- `lhctl` installed and configured.
- A compatible LittleHorse Server running. See the shared [server setup](../../README.md#littlehorse-server-version).
- Verify connectivity with `lhctl whoami`.

The application creates `LHConfig` with `new LHConfig()`, so `LHC_*` environment variables select a remote server when needed.

## Register The Workflow

From the `lh-developer-hub` repository root, run this exact command:

```bash
./gradlew -p examples/lh-server/java/00-quickstart run --args register
```

The command registers the three `TaskDef`s, the typed `identity-verified` `ExternalEventDef`, and the `quickstart` `WfSpec`.

## Drive The Workflow

Start a run before the workers so you can see it wait in `TASK_SCHEDULED`:

```bash
lhctl run quickstart full-name 'Grace Hopper' email grace@example.com ssn 987654321
```

Start the workers in a second terminal:

```bash
./gradlew -p examples/lh-server/java/00-quickstart run --args workers
```

The run is `RUNNING` while it waits for the correlated event. Complete it with `true`:

```bash
lhctl put correlatedEvent grace@example.com identity-verified BOOL true
```

Use `BOOL false` to execute the not-verified branch. To exercise the timeout branch, start a run and do not send its event; the timeout is five minutes.

Expected task output includes:

```text
Verification request accepted for Grace Hopper at grace@example.com
Notified Grace Hopper that identity was verified
```

The terminal state for the true and false paths is `COMPLETED`. The timeout path is `ERROR` after the notification and the `customer-not-verified` failure.

The identity task's retry policy is for technical failures and timeouts. A named `LHTaskException` represents a business exception and is not retried by this policy.

## Inspect A Run

```bash
lhctl get wfRun <wfRunId>
lhctl list nodeRun <wfRunId>
lhctl get taskRun <wfRunId> <taskRunGlobalId>
lhctl search variable --name email --value grace@example.com --varType STR --wfSpecName quickstart --wfSpecMajorVersion 0 --wfSpecRevision 0
```

The `WfRun` output shows the final `identity-verified` value and the event wait. `list nodeRun` shows the verification, event, and notification nodes. `get taskRun` shows task attempts, including retries if a worker-side technical error is introduced while experimenting with `QuickstartTasks`.

## WfSpec And WfRun

`QuickstartWorkflow` runs once at startup to author and register a graph. It does not execute Java business logic. Later, the server creates a `WfRun`; task workers execute `QuickstartTasks` only when that run reaches a task node. The shutdown hook closes every `LHTaskWorker`.

## Source Files

- [`QuickstartWorkflow.java`](./src/main/java/io/littlehorse/examples/QuickstartWorkflow.java) defines the graph, event wait, retry, and timeout handler.
- [`QuickstartTasks.java`](./src/main/java/io/littlehorse/examples/QuickstartTasks.java) contains runtime task methods.
- [`QuickstartApplication.java`](./src/main/java/io/littlehorse/examples/QuickstartApplication.java) provides the separate `register` and `workers` commands.

## Common Failure Modes

- `lhctl whoami` fails: the standalone server is not running, or the `LHC_*` endpoint/auth environment is not configured.
- A run remains at a task node: the matching worker process is not running or its task definition was not registered.
- A run remains at the event node: send a correlated event with the exact email key and event name.
- Registration reports an existing incompatible event or spec: remove the old development metadata or use a new name/version before retrying.
