# LittleHorse TypeScript Concept Examples

Runnable TypeScript counterparts for every SDK example in the LittleHorse server concept documentation.

## Prerequisites

- Node.js 20 or newer
- A LittleHorse server reachable through `LHC_*` environment variables, or at the SDK defaults
- `lhctl` for the printed command-line triggers

Install and compile everything:

```bash
cd examples/lh-server/typescript
npm install
npm run build
```

Each main script registers its `TaskDef`s and `WfSpec`, starts its task workers, and prints the command needed to run it. Keep that process running while invoking the trigger or posting command from another terminal.

## Examples

| Concept | Start command | Trigger or follow-up |
| --- | --- | --- |
| Tasks | `npm run tasks` | Worker only; used by the Workflows example |
| Workflows | `npm run workflows` | `lhctl run quickstart name Obi-Wan` |
| Variables | `npm run variables` | `lhctl run variables-example user-id obiwan` |
| Conditionals | `npm run conditionals` | `lhctl run send-message user-id anakin message "Hello there"` |
| Threads | `npm run threads` | `lhctl run threads-example` |
| Child Workflows | `npm run child-workflows` | `lhctl run greeting-parent input-name "Obi-Wan"` |
| Exception Handling | `npm run exceptions` | `lhctl run exception-example price 50.0` |
| External Events | `npm run external-events` | Start a run, then `npm run external-events:post -- <wf-run-id>` |
| Interrupts | `npm run interrupts` | Start `my-wf`, then `npm run interrupts:post -- my-wf` |
| Correlated Events | `npm run correlated-events` | Start a run, then `npm run correlated-events:post` |
| User Tasks | `npm run user-tasks` | `npm run user-tasks:run` |
| StructDefs | `npm run structdefs` | `npm run structdefs:run` |
| Arrays | `npm run arrays` | Use the printed `lhctl run` command |
| Maps | `npm run maps` | Use the printed `lhctl run` command |

## Native Array Limitation

The current TypeScript task-worker schema API maps `z.array()` to `JSON_ARR`. It does not expose the native `ARRAY` input/output annotation available in Java and .NET, nor native `ARRAY` fields in a `StructDef`. The arrays example therefore exercises native arrays inside the workflow DSL, but does not claim native collection task I/O support.

The TypeScript WfSpec API requires a LittleHorse `1.3.0-RC3` or current `master` server.