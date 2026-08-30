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

The numbered names keep the examples in their recommended learning order. Entries marked **planned** will be added incrementally. The examples prefer LittleHorse's typed variables, arrays, maps, and structs over `JSON_OBJ` and `JSON_ARR`.

| Example | What you will learn |
| --- | --- |
| `00-quickstart` **planned** | Define tasks and the KYC workflow from the upstream quickstart, start workers, run a `WfRun`, and inspect it. |
| [`05-spring-boot`](./java/05-spring-boot/) | Start, signal, and inspect an event-driven order workflow through a Spring Boot REST API. |
| `10-javalin` **planned** | Expose a smaller LittleHorse REST integration using Javalin. |
| `15-advanced-variables` **planned** | Use typed arrays and maps, expressions, mutation, field access, and explicit casting. |
| `20-timestamps` **planned** | Pass timestamp variables between Java and LittleHorse, sleep for a duration, and sleep until a scheduled time. |
| `25-conditionals` **planned** | Build `doIf().doElseIf().doElse()` branches and `doWhile()` loops. |
| `30-child-threads` **planned** | Spawn and join child threads, then handle child errors and business exceptions. |
| `35-task-failures` **planned** | Distinguish technical errors from `LHTaskException`, configure retries, and handle exception content. |
| `40-checkpoint-tasks` **planned** | Checkpoint side effects across retries and inspect execution metadata with `WorkerContext`. |
| `45-external-events` **planned** | Wait for typed and correlated events, enforce a timeout, and recover through a failure handler. |
| `50-interrupts` **planned** | Handle a typed interrupt payload, mutate workflow state, and resume from `waitForCondition()`. |
| `55-structs` **planned** | Define structs, build nested structs dynamically in a `WfSpec`, access fields with `.get()`, and process `InlineStruct` in a worker. |
| `60-child-workflows` **planned** | Start a child `WfRun`, pass typed inputs, wait for it, and consume its output. |
| `65-hierarchical-workflows` **planned** | Relate parent and child `WfSpec`s and share public and inherited variables. |
| `70-user-tasks` **planned** | Define forms, assign and reassign user tasks, schedule reminders, and handle cancellation. |
| `75-workflow-events-output-topic` **planned** | Throw `WorkflowEvent`s with `wf.throwEvent()` and consume them from the Kafka output topic. |

Every workflow will define a retention policy between 7 and 30 days. Across the learning path, examples will also demonstrate workflow-level and task-level retry policies, `WorkerContext`, `LHTaskException`, `handleError()`, and `handleException()`.

The existing `taskdef-app`, `wfspec-app`, and `workflow-migrations` projects remain available as standalone references outside this learning path.
