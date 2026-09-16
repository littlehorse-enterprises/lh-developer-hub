import { createTaskWorker, LHConfig, Workflow } from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

function greet(name: string): string {
  const result = `Hello, ${name}!`;
  console.log(result);
  return result;
}

const config = LHConfig.from({});
const worker = createTaskWorker(greet, "greet", config, {
  inputVars: { name: z.string() },
  outputSchema: z.string(),
});

const child = Workflow.newWorkflow("greeting-child", (wf) => {
  const name = wf.declareStr("name").required();
  wf.complete(wf.execute("greet", name));
});

const parent = Workflow.newWorkflow("greeting-parent", (wf) => {
  const inputName = wf.declareStr("input-name").required();
  const childOutput = wf.declareStr("child-output");

  const childWf = wf.runWf("greeting-child", { name: inputName });
  wf.execute("greet", "hi from parent");
  childOutput.assign(wf.waitForChildWf(childWf));
});

await closeOnShutdown([worker]);
await worker.registerTaskDef();
await child.registerWfSpec(config);
await parent.registerWfSpec(config);
console.log('Run with: lhctl run greeting-parent input-name "Obi-Wan"');
await worker.start();