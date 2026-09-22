# Python Interrupts Example

Registers the documented `collect-underpants` WfSpec, events, and task workers.

From the repository root, complete the [shared Python setup](../README.md), then run:

```bash
python examples/lh-server/python/10-interrupts/main.py register
python examples/lh-server/python/10-interrupts/main.py workers
lhctl run collect-underpants --wfRunId my-run all-underpants '[]'
lhctl postEvent my-run underpant-collected STR Stan
lhctl postEvent my-run done-collecting-underpants
```