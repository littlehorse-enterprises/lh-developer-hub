import { config } from "../config.js";

const wfRunId = process.argv[2] ?? "my-wf";
const client = config.getClient();

for (const owner of ["Cartman", "Kyle", "Stan"]) {
  await client.putExternalEvent({
    wfRunId: { id: wfRunId },
    externalEventDefId: { name: "underpant-collected" },
    content: { value: { oneofKind: "str", str: owner } },
  });
}

await client.putExternalEvent({
  wfRunId: { id: wfRunId },
  externalEventDefId: { name: "done-collecting-underpants" },
  content: { value: { oneofKind: undefined } },
});