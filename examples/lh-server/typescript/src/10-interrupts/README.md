# Interrupts

This example installs an interrupt handler that collects event payloads while the main workflow waits for a completion event.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run interrupts
```

In another terminal, start the named run and post the sample interrupt sequence:

```bash
lhctl run collect-underpants --wfRunId my-wf all-underpants '[]'
npm run interrupts:post -- my-wf
```

The posting script sends three `underpant-collected` events followed by `done-collecting-underpants`, allowing the main thread to finish.