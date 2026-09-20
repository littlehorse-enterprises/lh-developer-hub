# User Tasks Bridge Quickstart

This example registers an IT request workflow, its user task form, and its task workers for the User Tasks Bridge Getting Started guide.

## Prerequisites

- Java 17 or newer (required by the repository's Gradle wrapper)
- Docker with at least 4 GB of RAM
- `lhctl`

Run all commands from the `lh-developer-hub` repository root. Start the User Tasks Bridge standalone environment:

```bash
docker run --pull always --name lh-user-tasks-bridge-standalone --rm -d \
  -p 8080:8080 -p 8888:8888 -p 8089:8089 \
  -p 3000:3000 -p 2023:2023 -p 9092:9092 \
  ghcr.io/littlehorse-enterprises/lh-user-tasks-bridge-backend/lh-user-tasks-bridge-standalone:0.15.2
```

After the environment is ready, register the metadata and start the workers:

```bash
./gradlew -p examples/user-tasks-bridge/00-quickstart run
```

In another terminal, start a workflow run:

```bash
lhctl run it-request item laptop employee obi-wan
```

Open the User Tasks Bridge Console at [http://localhost:3000](http://localhost:3000). Stop the worker with `Ctrl+C` and the environment with `docker stop lh-user-tasks-bridge-standalone`.
