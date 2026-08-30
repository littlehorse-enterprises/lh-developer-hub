# LittleHorse Server Examples

These examples are written for developers who know Java but are new to LittleHorse. Each example is an independent Gradle project, so you can copy one without bringing along shared build logic from this repository.

## Start LittleHorse

The only infrastructure required is the LittleHorse standalone image:

```bash
docker run --pull always --name lh-standalone --rm -d \
  -p 2023:2023 -p 8080:8080 -p 9092:9092 \
  ghcr.io/littlehorse-enterprises/littlehorse/lh-standalone:1.2.1
```

Verify that the server is ready:

```bash
lhctl whoami
```

Every example creates its client with `new LHConfig()`. `LHConfig` reads the `LHC_*` environment variables, so the same examples work with a remote LittleHorse Server, including LittleHorse Cloud, after you source the environment variables for that server.

## Java Learning Path

The numbered names keep the examples in their recommended learning order. Entries marked **planned** will be added incrementally.

| Example | What you will learn |
| --- | --- |
| `00-quickstart` **planned** | Define tasks and a KYC workflow, run workers, and start a `WfRun`. |
| [`05-spring-boot`](./java/05-spring-boot/) | Start and inspect event-driven order workflows through a Spring Boot REST API. |
| `10-javalin` **planned** | Integrate LittleHorse with a lightweight Javalin REST service. |
| `15-structs` **planned** | Model typed domain data with structs, collections, JSON, and timestamps. |
| `20-external-events` **planned** | Wait for, correlate, and time out external events. |
| `25-failure-handling` **planned** | Use retries, handlers, checkpoints, and worker context. |
| `30-user-tasks` **planned** | Build human approval flows with forms, users, groups, and reminders. |
| `35-parallelism` **planned** | Spawn threads, fan out dynamically, join work, and race outcomes. |
| `40-child-workflows` **planned** | Compose child workflows and understand hierarchical workflows. |
| `45-saga` **planned** | Compensate completed actions after a later operation fails. |
| `50-workflow-events` **planned** | Publish workflow events and await them from another application. |
| `55-output-topic` **planned** | Consume workflow state changes from Kafka. |

The existing `taskdef-app`, `wfspec-app`, and `workflow-migrations` projects remain available as standalone references outside this learning path.
