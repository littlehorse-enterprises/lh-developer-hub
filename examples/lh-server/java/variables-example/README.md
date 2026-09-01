# Variables Example App

A `WfSpec` that models a simple user notification workflow, demonstrating how to
declare, mutate, and search LittleHorse `Variable`s.

The workflow requires a `user-id` as input, loads information about the user via
the `fetch-user` task, and uses that information to formulate a message sent via
the `send-email` task.

## Variables
- `user-id` — `STR`, required as input and searchable.
- `user-obj` — `JSON_OBJ`, holds the serialized `User` returned by `fetch-user`.
- `age` — `INT`, assigned from a sub-field of the task output.

## Prerequisites
- Java 21+
- A running LittleHorse server

## Register wfSpec, taskDefs, and start workers
This registers the `fetch-user` and `send-email` task defs and the
`variables-example` `WfSpec`, then starts the workers and keeps the process
running.

From the repository root:

```sh
./gradlew :examples:lh-server:java:variables-example:run
```

## Run a WfRun
In a separate terminal, run the workflow. `user-id` is required, so passing none
fails:

```sh
lhctl run variables-example
```

Provide the required input variable:

```sh
lhctl run variables-example user-id anakin
```

Run another instance so you have more than one to search:

```sh
lhctl run variables-example user-id obiwan
```

## Search by variable
Search for `WfRun`s by the value of the searchable `user-id` variable:

```sh
lhctl search variable --wfSpecName variables-example --name user-id --varType STR --value anakin
```

Then inspect a specific variable using the `wfRunId`, thread number, and name
from the search results:

```sh
lhctl get variable <wfRunId> 0 user-id
```
