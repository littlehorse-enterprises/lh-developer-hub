# Python Correlated Events Example

Registers the documented `correlated-event-example` WfSpec and document worker.

From the repository root, complete the [shared Python setup](../README.md), then run:

```bash
python examples/lh-server/python/11-correlated-events/main.py register
python examples/lh-server/python/11-correlated-events/main.py workers
lhctl run correlated-event-example document-id my-document-abc123
lhctl put correlatedEvent my-document-abc123 document-signed STR Leia
```