# LittleHorse Quarkus Quickstart

This Quarkus application uses the LittleHorse extension to register an identity verification workflow and task workers, then exposes REST endpoints on port `9000`.

## Prerequisites

- Java 17 or newer
- Docker with at least 2 GB of RAM

Run all commands from the `lh-developer-hub` repository root. Start LittleHorse:

```bash
docker run --pull always --name lh-standalone --rm -d \
  -p 2023:2023 -p 8080:8080 -p 9092:9092 \
  ghcr.io/littlehorse-enterprises/littlehorse/lh-standalone:1.2.1
```

Start the application in development mode:

```bash
./gradlew -p examples/lh-quarkus/00-quickstart quarkusDev
```

Start a workflow through its REST API:

```bash
curl -X POST http://localhost:9000/identity-verification/start \
  -H 'Content-Type: application/json' \
  -d '{"fullName":"Obi-Wan Kenobi","email":"obiwankenobi@jedi.temple","ssn":123456789}'
```

See the [Quarkus Extension Getting Started guide](https://littlehorse.io/docs/getting-started/quarkus-extension) for the remaining API calls. Stop Quarkus with `Ctrl+C` and LittleHorse with `docker stop lh-standalone`.
