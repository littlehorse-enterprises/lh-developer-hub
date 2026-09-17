# Maps

This example declares native typed maps, merges map values, removes a value by key, and declares a map whose values are typed arrays.

## Run

From `examples/lh-server/typescript`, register the workflow:

```bash
npm run maps
```

Then start a workflow run:

```bash
lhctl run maps-example scores '{"alice":10}' scores-to-merge '{"bob":20}' key-to-remove alice
```
