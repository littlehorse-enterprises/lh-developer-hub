# Arrays and Maps

This example demonstrates native typed arrays and maps in the workflow DSL, including mutation, indexing, membership checks, nested collection types, and one child thread per array element.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run arrays-maps
```

In another terminal:

```bash
lhctl run arrays-and-maps my-array '[1,2,3]' value-to-check 3 my-map '{"one":1}'
```

## SDK Limitation

The current TypeScript worker schema API maps `z.array()` to `JSON_ARR`; it cannot declare native `ARRAY` task inputs, outputs, or `StructDef` fields. This example therefore keeps native arrays and maps inside the workflow DSL and passes scalar elements to workers.