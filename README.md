# LittleHorse Developer Hub

Examples and coding-agent skills for building applications with [LittleHorse](https://littlehorse.io).

## Start Here

| I want to... | Go to... |
| --- | --- |
| Run my first LittleHorse workflow | [Server quickstart](./examples/lh-server/README.md#quickstart-by-language) |
| Learn the Java SDK one concept at a time | [Java learning path](./examples/lh-server/README.md#java-learning-path) |
| Learn the Python SDK one concept at a time | [Python concept examples](./examples/lh-server/README.md#python-concept-examples) |
| Learn the Go SDK one concept at a time | [Go concept examples](./examples/lh-server/README.md#go-concept-examples) |
| Browse TypeScript examples | [TypeScript concept examples](./examples/lh-server/typescript/) |
| Build a User Tasks Bridge workflow | [User Tasks Bridge quickstart](./examples/user-tasks-bridge/00-quickstart/) |
| Build with the LittleHorse Quarkus extension | [Quarkus quickstart](./examples/lh-quarkus/00-quickstart/) |
| Start workflows from Kafka records | [Kafka Connect quickstart](./examples/kafka-connectors/00-quickstart/) |
| Give a coding agent LittleHorse SDK guidance | [WfSpec skills](./skills/) |
| Build with Saddle | [Saddle quickstart](./examples/saddle/00-quickstart/) |
| Produce or consume Saddle Streamlets | [Streamlet quickstart](./examples/saddle/05-streamlet/) |

## Run Your First Workflow

You need Docker, [`lhctl`](https://littlehorse.io/docs/getting-started/quickstart#system-setup), and Java 21 or newer for this default path.

Clone the repository and start the standalone LittleHorse Server, Dashboard, and Kafka broker:

```bash
git clone https://github.com/littlehorse-enterprises/lh-developer-hub.git
cd lh-developer-hub

docker run --pull always --name lh-standalone --rm -d \
  -p 2023:2023 -p 8080:8080 -p 9092:9092 \
  ghcr.io/littlehorse-enterprises/littlehorse/lh-standalone:1.2.1

lhctl whoami
```

Register the Java quickstart metadata:

```bash
./gradlew -p examples/lh-server/java/00-quickstart run --args register
```

Start a workflow run before its workers so you can see the task wait in `TASK_SCHEDULED`:

```bash
lhctl run quickstart \
  full-name 'Grace Hopper' \
  email grace@example.com \
  ssn 987654321
```

Start the workers in a second terminal:

```bash
./gradlew -p examples/lh-server/java/00-quickstart run --args workers
```

Complete the workflow by sending its correlated event:

```bash
lhctl put correlatedEvent grace@example.com identity-verified BOOL true
```

Open the Dashboard at [http://localhost:8080](http://localhost:8080), or use `lhctl get wfRun <wfRunId>` to inspect the run. When finished, stop the workers with `Ctrl+C` and remove the local server with `docker stop lh-standalone`.

The same quickstart is runnable in four languages:

| Language | Requirements | Walkthrough |
| --- | --- | --- |
| Java | Java 21+ | [`java/00-quickstart`](./examples/lh-server/java/00-quickstart/) |
| Python | Python 3.10-3.13 | [`python/00-quickstart`](./examples/lh-server/python/00-quickstart/) |
| Go | Go 1.24+ | [`go/00-quickstart`](./examples/lh-server/go/00-quickstart/) |
| .NET | .NET 8+ | [`dotnet/00-quickstart`](./examples/lh-server/dotnet/00-quickstart/) |

## Choose Compatible Examples

| Area | Client/server version | Additional requirements |
| --- | --- | --- |
| Four-language quickstart | LittleHorse `1.2.1` | Language runtime listed above |
| Java and Python concept examples | Client `1.3.0`; compatible server | Java 21+ or Python 3.10-3.13 |
| Go concept examples | Client `1.3.0`; compatible server | Go 1.25+ |
| TypeScript concept examples | Client `1.3.0`; compatible 1.3 server | Node.js 20+ |
| Saddle examples | Version is environment-specific | Access to a Saddle environment |

LittleHorse clients read `LHC_*` environment variables. The same example code can connect to the local standalone server, another LittleHorse deployment, or LittleHorse Cloud when those variables are configured.

## Browse More Examples

The [LittleHorse Server examples guide](./examples/lh-server/README.md) contains the ordered Java catalog, Go and TypeScript concept collections, four-language quickstart, and build commands.

[User Tasks Bridge](./examples/user-tasks-bridge/00-quickstart/) demonstrates identity-backed user tasks, the [Quarkus extension](./examples/lh-quarkus/00-quickstart/) manages workflow and worker lifecycles in a Quarkus application, and [Kafka Connect](./examples/kafka-connectors/00-quickstart/) starts workflows from Kafka records.

[Saddle](./examples/saddle/) adds managed workflow and streaming services around LittleHorse. Start with its Workflow Builder quickstart, then continue to the schema-backed Streamlet producer and consumer.

## Use The Coding-Agent Skills

Load the `SKILL.md` for the SDK you are using before asking an agent to create or review a `WfSpec`:

- [Java](./skills/littlehorse-java-wfspec/SKILL.md)
- [Python](./skills/littlehorse-python-wfspec/SKILL.md)
- [Go](./skills/littlehorse-go-wfspec/SKILL.md)
- [.NET](./skills/littlehorse-dotnet-wfspec/SKILL.md)

These references cover variables, tasks, expressions, control flow, events, threads, child workflows, failures, user tasks, and structs. The central mental model is the same in every language: authoring code builds and registers a `WfSpec` graph; a `WfRun` executes that graph later, and task workers perform runtime work.

## Build The Examples

```bash
# All Java server examples
./gradlew buildJavaExamples

# Go concept examples
go -C examples/lh-server/go test ./...

# Python concept examples
python examples/lh-server/python/validate.py

# TypeScript concept examples
cd examples/lh-server/typescript
npm install
npm run build
```

The Java examples are independent Gradle projects. The Python, Go, and .NET quickstart READMEs contain their language-specific validation commands.

## Repository Layout

```text
examples/lh-server/java/        Java quickstart and learning path
examples/lh-server/python/      Python quickstart and concept examples
examples/lh-server/go/          Go quickstart and concept examples
examples/lh-server/dotnet/      .NET quickstart
examples/lh-server/typescript/  TypeScript concept examples
examples/user-tasks-bridge/     User Tasks Bridge user-task quickstart
examples/lh-quarkus/            Quarkus extension quickstart
examples/kafka-connectors/      Kafka Connect quickstart
examples/saddle/                Saddle workflow and streaming examples
skills/                         WfSpec references for coding agents
```

## Help And Links

- [LittleHorse documentation](https://littlehorse.io/docs)
- [LittleHorse Server source](https://github.com/littlehorse-enterprises/littlehorse)
- [LittleHorse community Slack](https://launchpass.com/littlehorsecommunity/free)
- [Apache 2.0 license](./LICENSE)
