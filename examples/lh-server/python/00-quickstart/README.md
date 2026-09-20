# Python Quickstart: KYC Workflow

This is the Python implementation used by the [LittleHorse quickstart](https://littlehorse.io/docs/getting-started/quickstart). It registers the same `quickstart` workflow and metadata as the Java, Go, and .NET versions.

```bash
cd examples/lh-server/python/00-quickstart
python -m venv .venv
source .venv/bin/activate
python -m pip install -r requirements.txt
python -m quickstart.register
```

Start a run, then start the workers in a second terminal with the virtual environment active:

```bash
lhctl run quickstart full-name 'Grace Hopper' email grace@example.com ssn 987654321
python -m quickstart.workers
lhctl put correlatedEvent grace@example.com identity-verified BOOL true
```

See the [Java walkthrough](../../java/00-quickstart/README.md) for the workflow behavior and inspection commands shared by every language.
