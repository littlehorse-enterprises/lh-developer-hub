# LittleHorse Kafka Connect Quickstart

This example starts LittleHorse, Kafka, and a standalone Kafka Connect worker with the LittleHorse `WfRunSinkConnector`. Records sent to `new-customers` start the hub's canonical `quickstart` workflow.

## Prerequisites

- Docker with host networking support
- Java 21 or newer
- `curl` and `unzip`

Run all commands from the `lh-developer-hub` repository root. Start the services and configure the connector:

```bash
./examples/kafka-connectors/00-quickstart/setup.sh
```

Register the workflow:

```bash
./gradlew -p examples/lh-server/java/00-quickstart run --args register
```

Start its workers in another terminal:

```bash
./gradlew -p examples/lh-server/java/00-quickstart run --args workers
```

Start a Kafka producer in a third terminal:

```bash
docker run -it --rm --net=host apache/kafka:4.0.0 \
  /opt/kafka/bin/kafka-console-producer.sh \
  --topic new-customers \
  --bootstrap-server localhost:9092
```

Send records using the workflow's exact variable names:

```json
{"full-name":"Obi-Wan Kenobi","email":"obi-wan@example.com","ssn":12345}
{"full-name":"Anakin Skywalker","email":"anakin@example.com","ssn":54321}
```

Open the Dashboard at [http://localhost:8080](http://localhost:8080). When finished, stop the workers with `Ctrl+C`, then remove only this quickstart's containers and downloaded connector files:

```bash
./examples/kafka-connectors/00-quickstart/cleanup.sh
```

The setup uses Docker host networking, which may not be available or enabled in every Docker Desktop installation.
