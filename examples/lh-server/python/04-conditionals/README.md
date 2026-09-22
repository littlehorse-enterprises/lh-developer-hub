# Python Conditionals Example

Registers the `send-message` WfSpec and its documented contact-method workers.

From the repository root, complete the [shared Python setup](../README.md), then run:

```bash
python examples/lh-server/python/04-conditionals/main.py register
python examples/lh-server/python/04-conditionals/main.py workers
lhctl run send-message user-id anakin message "Hello there"
```