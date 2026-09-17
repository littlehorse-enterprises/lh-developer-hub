import { createTaskWorker, LHConfig, Workflow } from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

function greet(name: string): string {
  const result = `Hello, ${name}!`;
  console.log(result);
  return result;
}

const config = LHConfig.from({});
const client = config.getClient();
const worker = createTaskWorker(greet, "greet", config, {
  inputVars: { name: z.string() },
  outputSchema: z.string(),
});

await client.putExternalEventDef({ name: "name-posted", contentType: {} });

const workflow = Workflow.newWorkflow("greet-event", (wf) => {
  const name = wf.declareStr("name");
  name.assign(wf.waitForEvent("name-posted"));
  wf.execute("greet", name);
});

await closeOnShutdown([worker]);
await worker.registerTaskDef();
await workflow.registerWfSpec(config);
console.log("Run with: lhctl run greet-event");
console.log("Then post with: npm run external-events:post -- <wf-run-id>");
await worker.start();