# Python Server Examples

The numbered directories are runnable counterparts for the frozen Python snippets in the LittleHorse server concepts documentation. The existing [`00-quickstart`](./00-quickstart/) remains independent because its commands are already published.

## Setup

From the repository root, create one shared environment and install the pinned SDK:

```bash
python3.13 -m venv examples/lh-server/python/.venv
source examples/lh-server/python/.venv/bin/activate
python -m pip install -r examples/lh-server/python/requirements.txt
```

Run a compatible LittleHorse Server as described in the [server examples guide](../README.md), then use each concept README's `register` and `workers` commands.

## Validation

The validation command compiles every example and constructs every WfSpec without connecting to a server:

```bash
python examples/lh-server/python/validate.py
```

The examples use `littlehorse-client==1.3.0`. To use another client version, update [`requirements.txt`](./requirements.txt), recreate the shared environment, and run the validation command again. The server version is configured separately in the [server examples guide](../README.md).

Native `ARRAY` and `MAP` examples are omitted because those APIs are not available in the Python SDK.

## Concepts

- [`01-tasks`](./01-tasks/)
- [`02-workflows`](./02-workflows/)
- [`03-variables`](./03-variables/)
- [`04-conditionals`](./04-conditionals/)
- [`06-threads`](./06-threads/)
- [`07-child-workflows`](./07-child-workflows/)
- [`08-exception-handling`](./08-exception-handling/)
- [`09-external-events`](./09-external-events/)
- [`10-interrupts`](./10-interrupts/)
- [`11-correlated-events`](./11-correlated-events/)
- [`16-user-tasks`](./16-user-tasks/)
- [`21-structdefs`](./21-structdefs/)