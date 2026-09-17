import { config } from "../config.js";

const wfRunId = process.argv[2];
if (!wfRunId) {
  throw new Error("Usage: npm run external-events:post -- <wf-run-id>");
}

const result = await config.getClient().putExternalEvent({
  wfRunId: { id: wfRunId },
  externalEventDefId: { name: "name-posted" },
  content: { value: { oneofKind: "str", str: "Obi-Wan Kenobi" } },
});
console.log(result);