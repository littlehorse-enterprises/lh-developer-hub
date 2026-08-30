# Structs

This example builds nested LittleHorse structs in a WfSpec and passes them to typed Java task methods.

## Struct Definitions

The annotated POJOs form a nested schema:

```text
PurchaseOrder
  +-- CustomerProfile
  |     +-- Address
  +-- LineItem
```

The corresponding StructDef names are `purchase-order`, `customer-profile`, `address`, and `line-item`. They are registered in dependency order so nested definitions exist before the definitions that reference them.

## Workflow

The `nested-structs-demo` workflow requires `customer-id`, `order-id`, `sku`, and `quantity` variables.

1. `lookup-address` returns a typed `Address` POJO.
2. `normalize-address` accepts and returns a typed `InlineStruct`. Its parameter and return value use `@LHType(structDefName = "address")`.
3. `buildStruct("customer-profile")` assembles a `CustomerProfile` from workflow values.
4. `buildInlineStruct()` creates the nested `Address` and `LineItem` values.
5. `customer.get("address").get("city")` demonstrates nested field access on a `WfRunVariable`, while `normalizedAddress.get("city")` accesses a field on a `NodeOutput`.
6. `save-order` receives the completed `PurchaseOrder` POJO.

The workflow retains completed runs for 24 hours and thread records for one hour after termination.

## Prerequisites

- Java 21+
- A running LittleHorse Server
- `lhctl` configured for that server

The module is independent: it uses `io.littlehorse:littlehorse-client:1.2.1`, its own Java 21 toolchain, and `new LHConfig()`. `new LHConfig()` reads the `LHC_*` environment variables.

## Run

From this directory:

```bash
../../../../gradlew run
```

Startup registers the nested StructDefs, task definitions, and WfSpec before starting the workers. Stop the process with Ctrl-C; the shutdown hook closes every worker.

## Try It

Start a run in another terminal. `lhctl` uses the WfSpec variable names as the arguments:

```bash
lhctl run nested-structs-demo \
  customer-id C-42 \
  order-id O-100 \
  sku holocron \
  quantity 2
```

Inspect the run and task nodes:

```bash
lhctl get wfRun <wf_run_id>
lhctl list nodeRun <wf_run_id>
lhctl list taskRun <wf_run_id>
```
