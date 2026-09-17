# StructDefs

This example defines named Zod structures with `lhStruct`, registers their dependencies as LittleHorse `StructDef`s, uses a struct as task input, and declares a typed workflow variable.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run structdefs
```

In another terminal, start a workflow with a type-checked `Car` value:

```bash
npm run structdefs:run
```

The runner converts the JavaScript object with `toStructVariableValue` before sending it to LittleHorse.