# Arrays

This example declares a native typed array, applies array mutations, reads its size and elements, checks whether it contains a value, and processes its elements in child threads.

## Run

From `examples/lh-server/typescript`, start the application:

```bash
npm run arrays
```

In another terminal:

```bash
lhctl run arrays-example my-array '[1,2,3]' value-to-check 3
```

Keep the workers running until the workflow completes, then stop them with `Ctrl+C`.
