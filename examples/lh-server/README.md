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

The numbered names keep the examples in their recommended learning order. The examples prefer LittleHorse's typed variables, arrays, maps, and structs over `JSON_OBJ` and `JSON_ARR`.

| Example | What you will learn |
| --- | --- |
| [`00-quickstart`](./java/00-quickstart/) | Define tasks and the KYC workflow from the upstream quickstart, start workers, run a `WfRun`, and inspect it. |
| [`05-spring-boot`](./java/05-spring-boot/) | Start, signal, and inspect an event-driven order workflow through a Spring Boot REST API. |
| [`10-javalin`](./java/10-javalin/) | Expose a smaller LittleHorse REST integration using Javalin. |
| [`15-advanced-variables`](./java/15-advanced-variables/) | Use typed arrays and maps, expressions, mutation, field access, and explicit casting. |
| [`20-timestamps`](./java/20-timestamps/) | Pass timestamp variables between Java and LittleHorse and format them in task workers. |
| [`25-conditionals`](./java/25-conditionals/) | Build `doIf().doElseIf().doElse()` branches from typed workflow expressions. |
| [`30-child-threads`](./java/30-child-threads/) | Spawn and join child threads, then handle child errors and business exceptions. |
| [`35-task-failures`](./java/35-task-failures/) | Distinguish technical errors from `LHTaskException`, configure retries, and handle exception content. |
| [`40-checkpoint-tasks`](./java/40-checkpoint-tasks/) | Checkpoint side effects across retries and inspect execution metadata with `WorkerContext`. |
| [`45-external-events`](./java/45-external-events/) | Wait for typed and correlated events, enforce a timeout, and recover through a failure handler. |
| [`50-interrupts`](./java/50-interrupts/) | Handle a typed interrupt payload, mutate workflow state, and resume from `waitForCondition()`. |
| [`55-structs`](./java/55-structs/) | Define structs, build nested structs dynamically in a `WfSpec`, access fields with `.get()`, and process `InlineStruct` in a worker. |
| [`60-child-workflows`](./java/60-child-workflows/) | Start a child `WfRun`, pass typed inputs, wait for it, and consume its output. |
| [`65-hierarchical-workflows`](./java/65-hierarchical-workflows/) | Relate parent and child `WfSpec`s and share public and inherited variables. |
| [`70-user-tasks`](./java/70-user-tasks/) | Define forms, assign and reassign user tasks, schedule reminders, and handle cancellation. |
| [`75-workflow-events-output-topic`](./java/75-workflow-events-output-topic/) | Throw `WorkflowEvent`s with `wf.throwEvent()` and consume them from the Kafka output topic. |

The numbered examples use retention policies and retry policies appropriate to the feature being demonstrated. Across the learning path, examples also demonstrate workflow-level and task-level retries, `WorkerContext`, `LHTaskException`, `handleError()`, and `handleException()`.

The existing `taskdef-app`, `wfspec-app`, and `workflow-migrations` projects remain available as standalone references outside this learning path.
