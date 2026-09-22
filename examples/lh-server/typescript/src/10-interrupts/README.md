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
lhctl postEvent my-wf underpant-collected STR anakin
lhctl postEvent my-wf underpant-collected STR yoda
lhctl postEvent my-wf done-collecting-underpants
```

The `underpant-collected` events invoke the interrupt handler. The payload-free `done-collecting-underpants` event allows the main thread to finish.