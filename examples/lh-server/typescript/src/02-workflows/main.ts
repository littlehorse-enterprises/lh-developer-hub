import { createTaskWorker, Workflow } from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown, config } from "../config.js";

function greet(name: string): string {
  const result = `Hello ${name}!`;
  console.log(result);
  return result;
}

const worker = createTaskWorker(greet, "greet", config, {
  inputVars: { name: z.string() },
  outputSchema: z.string(),
});

const workflow = Workflow.newWorkflow("quickstart", (wf) => {
  const name = wf.declareStr("name").searchable().required();
  wf.execute("greet", name);
});

await closeOnShutdown([worker]);
await worker.registerTaskDef();
await workflow.registerWfSpec(config);
await worker.start();