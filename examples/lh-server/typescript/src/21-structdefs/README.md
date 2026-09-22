# StructDefs

This example defines the documented `Car` Zod structure with `lhStruct`, registers it as a LittleHorse `StructDef`, uses it as task input, and declares the typed `input-car` workflow variable.

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