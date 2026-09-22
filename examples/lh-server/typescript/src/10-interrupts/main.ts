import { createTaskWorker, LHConfig, Workflow } from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

function startCollection(): void {
  console.log("Starting collection of underpants!");
}

function collectUnderpant(underpantOwner: string): string {
  const result = `Successfully collected underpant from ${underpantOwner}`;
  console.log(result);
  return result;
}

function profit(underpants: unknown[]): string {
  const result = `Collected ${underpants.length} underpants!`;
  console.log(result);
  return result;
}

const config = LHConfig.from({});
const client = config.getClient();
const workers = [
  createTaskWorker(startCollection, "start-underpants-collection", config, {
    inputVars: {},
  }),
  createTaskWorker(collectUnderpant, "collect-underpant", config, {
    inputVars: { underpantOwner: z.string() },
    outputSchema: z.string(),
  }),
  createTaskWorker(profit, "profit", config, {
    inputVars: { underpants: z.array(z.unknown()) },
    outputSchema: z.string(),
  }),
];

await client.putExternalEventDef({ name: "underpant-collected", contentType: {} });
await client.putExternalEventDef({
  name: "done-collecting-underpants",
  contentType: {},
});

const workflow = Workflow.newWorkflow("collect-underpants", (wf) => {
  const allUnderpants = wf.declareJsonArr("all-underpants");
  wf.execute("start-underpants-collection");
  wf.waitForEvent("done-collecting-underpants");
  wf.execute("profit", allUnderpants);

  wf.registerInterruptHandler("underpant-collected", (handler) => {
    const interruptContent = handler.declareStr("INPUT");
    allUnderpants.assign(allUnderpants.add(interruptContent));
    handler.execute("collect-underpant", interruptContent);
  });
});

await closeOnShutdown(workers);
await Promise.all(workers.map((worker) => worker.registerTaskDef()));
await workflow.registerWfSpec(config);
console.log("Run with: lhctl run collect-underpants --wfRunId my-wf all-underpants '[]'");
console.log("Then post interrupts with: lhctl postEvent my-wf underpant-collected STR anakin");
console.log("Finish with: lhctl postEvent my-wf done-collecting-underpants");
await Promise.all(workers.map((worker) => worker.start()));