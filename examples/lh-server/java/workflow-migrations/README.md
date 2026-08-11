# Workflow Migrations App

This example is split into four separate runs so you can migrate a workflow in stages.

## Prerequisites
- Java 21+
- A running LittleHorse server

## Step 1: Register wfSpecs, taskDefs, and workers
This registers task defs, external events, and old/new workflow versions.
It also starts the workers and keeps the process running.

From the repository root:

```sh
./gradlew :examples:lh-server:java:workflow-migrations:run --args='step1'
```

## Step 2: Register migration plan
Run this in a separate terminal after step 1 is running.

From the repository root:

```sh
./gradlew :examples:lh-server:java:workflow-migrations:run --args='step2'
```

At this point we can start a wfRun.

```sh
lhctl run onboarding-workflow --majorVersion 0 --revision 0
```

## Step 3: Send migration request
Run this in a separate terminal after steps 1 and 2 are complete.
Provide an existing WfRun ID to apply the migration plan.

From the repository root:

```sh
./gradlew :examples:lh-server:java:workflow-migrations:run --args='step3 <wfRunId>'
```


## Step 4: Send migration request with variable override
Run this in a separate terminal after steps 1 and 2 are complete.
This applies migration and sets variable `name` on the destination thread to `obi-wan`.

From the repository root:

```sh
./gradlew :examples:lh-server:java:workflow-migrations:run --args='step4 <wfRunId>'
```

