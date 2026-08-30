# Checkpoint Tasks

This standalone Java 21 example demonstrates how `WorkerContext.executeAndCheckpoint()` makes a side effect durable across task retries. The task logs execution metadata, performs a side effect inside a checkpoint, and then intentionally fails after that checkpoint on attempt zero.

## Prerequisites

- Java 21 or newer.
- Docker, if running LittleHorse locally.
- `lhctl` configured for the same server, or the `LHC_*` environment variables used by `LHConfig`.

Start the local server if needed:

```sh
docker run --pull always --name lh-standalone --rm -d \
  -p 2023:2023 -p 8080:8080 -p 9092:9092 \
  ghcr.io/littlehorse-enterprises/littlehorse/lh-standalone:1.2.1
lhctl whoami
```

## Run

From this directory:

```sh
../../../../gradlew run
```

The application registers the `checkpointed-side-effect` task and the `checkpoint-tasks` WfSpec, then starts the worker.

In another terminal, run the workflow:

```sh
lhctl run checkpoint-tasks name "Qui-Gon Jinn"
```

The task allows two retries. On attempt zero it prints the side effect and then fails after the checkpoint. On the retry, LittleHorse restores the checkpoint result without executing the side effect again, and the task completes.

The worker also prints `wfRun`, `taskRun`, and `attempt` metadata from `WorkerContext`. The checkpoint callback writes a checkpoint log entry with `checkpointContext.log()`.

## Inspect a Run

Use the workflow run ID printed by `lhctl`:

```sh
lhctl get wfRun <wf-run-id>
lhctl list nodeRun <wf-run-id>
lhctl get taskRun <wf-run-id> <task-run-global-id>
```

The task run shows the failed first attempt and successful retry. Stop the application with `Ctrl-C`; its shutdown hook closes the worker cleanly.
