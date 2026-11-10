# Checkpointed Tasks

With LittleHorse running, start the worker from the repository root:

```sh
npm --prefix examples/lh-server/typescript install
npm --prefix examples/lh-server/typescript run checkpointed-tasks
```

In another terminal, run the workflow:

```sh
lhctl run checkpoint-tasks name obiwan
```

Look for these lines in the worker terminal:

```text
SIDE EFFECT: send greeting for obiwan
after checkpoint on attempt 0
after checkpoint on attempt 1
```

The first attempt intentionally fails after saving the checkpoint. The retry reuses the saved result, so `SIDE EFFECT` prints only once even though the task runs twice.

Register and run one language version at a time; all versions share the `checkpoint-tasks` WfSpec.