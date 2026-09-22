# Python External Events Example

Registers the documented `external-event-example` WfSpec and `greet` worker.

From the repository root, complete the [shared Python setup](../README.md), then run:

```bash
python examples/lh-server/python/09-external-events/main.py register
python examples/lh-server/python/09-external-events/main.py workers
lhctl run external-event-example --wfRunId my-run
lhctl postEvent my-run name-posted STR Obi-Wan
```