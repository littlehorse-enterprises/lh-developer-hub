# LittleHorse Developer Hub

The LittleHorse Developer Hub is the home for tested examples and AI coding skills for building applications with [LittleHorse](https://littlehorse.io). Start with the open-source LittleHorse Server, use the skills while writing your own workflows, or explore Saddle's managed workflow and streaming features.

## Choose A Path

| Path | Start here | What it covers |
| --- | --- | --- |
| LittleHorse Server | [`examples/lh-server/java/00-quickstart`](./examples/lh-server/java/00-quickstart/) | Business-as-Code, task workers, events, workflow runs, and the LittleHorse SDK. |
| WfSpec skills | [`skills/`](./skills/) | Agent-ready references for authoring workflows in Java, Go, Python, and .NET. |
| Saddle | [`examples/saddle/00-quickstart`](./examples/saddle/00-quickstart/) | The visual Workflow Builder, managed workers, webhooks, Streamlets, and triggers. |
| Saddle streaming | [`examples/saddle/05-streamlet`](./examples/saddle/05-streamlet/) | Produce and consume schema-validated Kafka records through StreamSense. |

## Get Started With LittleHorse Server

You need Docker, Java 21 or newer, and [`lhctl`](https://littlehorse.io/docs/getting-started/quickstart#install-lhctl). On macOS, install the CLI with:

```bash
brew install littlehorse-enterprises/lh/lhctl
```

Start the standalone LittleHorse Server, Dashboard, and Kafka broker:

```bash
docker run --pull always --name lh-standalone --rm -d \
  -p 2023:2023 -p 8080:8080 -p 9092:9092 \
  ghcr.io/littlehorse-enterprises/littlehorse/lh-standalone:1.2.1
```

Verify the connection:

```bash
lhctl whoami
```

Then run the first example from this repository's root:

```bash
./gradlew -p examples/lh-server/java/00-quickstart run
```

The application registers a `WfSpec` and its `TaskDef`s, starts the task workers, and creates a sample `WfRun`. It remains running because workers are long-lived processes. Open the Dashboard at [http://localhost:8080](http://localhost:8080), or inspect the run with:

```bash
lhctl get wfRun <wfRunId>
```

Follow the [quickstart walkthrough](./examples/lh-server/java/00-quickstart/README.md) to send its correlated event and complete the workflow.

## Tour The Server Examples

The numbered Java examples form a learning path. After the quickstart, continue with Spring Boot or Javalin integrations, then work through variables, conditions, concurrency, failures, events, interrupts, structs, child workflows, user tasks, and the Kafka output topic.

See the [LittleHorse Server examples guide](./examples/lh-server/README.md) for the complete ordered catalog and commands. Every example is an independent Gradle project, so it can also be copied into another workspace without relying on shared build logic.

Build all Java server examples from the repository root with:

```bash
./gradlew buildJavaExamples
```

The examples use `new LHConfig()`, which reads `LHC_*` environment variables. With the appropriate configuration, the same code can connect to the local standalone server, another LittleHorse deployment, or LittleHorse Cloud.

## Use The WfSpec Skills

The [`skills`](./skills/) directory contains focused references that coding agents can load while creating or reviewing a LittleHorse `WfSpec`:

- [`littlehorse-java-wfspec`](./skills/littlehorse-java-wfspec/SKILL.md)
- [`littlehorse-go-wfspec`](./skills/littlehorse-go-wfspec/SKILL.md)
- [`littlehorse-python-wfspec`](./skills/littlehorse-python-wfspec/SKILL.md)
- [`littlehorse-dotnet-wfspec`](./skills/littlehorse-dotnet-wfspec/SKILL.md)

Each skill covers the language's real SDK syntax for variables, task nodes, expressions, control flow, events, threads, child workflows, failures, user tasks, and structs. Give the relevant `SKILL.md` to an agent directly, or install it using your agent's skill mechanism, before asking the agent to author LittleHorse workflow code.

The central mental model is the same in every language: WfSpec authoring code builds and registers a graph; it does not execute the business process. A `WfRun` executes that graph later, and task workers perform the runtime work when the server schedules a `TaskRun`.

## Explore Saddle

Saddle adds a management UI and platform services around LittleHorse. These examples assume that you have access to a Saddle environment.

Start with the [Saddle quickstart](./examples/saddle/00-quickstart/), which walks through a complete integration using a managed or local task worker, the Workflow Builder, a webhook-backed Streamlet, and a workflow trigger.

Next, use the [Streamlet quickstart](./examples/saddle/05-streamlet/) to access Saddle's Kafka features. It walks through creating a schema-backed Streamlet and StreamSense/Schemas clients in the UI, then runs one Java application as either a Kafka producer or consumer.

## Repository Layout

```text
examples/lh-server/   Open-source LittleHorse Server examples
examples/saddle/      Saddle workflow and streaming quickstarts
skills/               WfSpec authoring skills for coding agents
```

## Learn More

- [LittleHorse documentation](https://littlehorse.io/docs)
- [LittleHorse Server source](https://github.com/littlehorse-enterprises/littlehorse)
- [LittleHorse community Slack](https://launchpass.com/littlehorsecommunity/free)
