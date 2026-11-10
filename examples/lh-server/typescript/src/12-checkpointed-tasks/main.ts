import { createTaskWorker, LHConfig, WorkerContext, Workflow } from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

async function greeting(name: string, context: WorkerContext): Promise<string> {
  const attemptNumber = context.getAttemptNumber();
  console.log(
    `metadata: wfRun=${context.getWfRunId()?.id} taskRun=${context.getTaskRunId()?.taskGuid} attempt=${attemptNumber}`,
  );

  const sideEffectReceipt = await context.executeAndCheckpoint<string>((checkpointContext) => {
    checkpointContext.log("sending the greeting side effect");
    console.log("SIDE EFFECT: send greeting for " + name);
    return "receipt-for-" + name;
  });

  console.log("after checkpoint on attempt " + attemptNumber);
  if (attemptNumber === 0) {
    throw new Error("Intentional post-checkpoint failure on the first attempt.");
  }

  return sideEffectReceipt + "; completed on attempt " + attemptNumber;
}

const config = LHConfig.from({});
const worker = createTaskWorker(greeting, "checkpoint-greet-typescript", config, {
  inputVars: { name: z.string() },
  outputSchema: z.string(),
});

const workflow = Workflow.newWorkflow("checkpoint-tasks", (wf) => {
  const name = wf.declareStr("name").required();
  wf.execute("checkpoint-greet-typescript", name).withRetries(2);
});

await closeOnShutdown([worker]);
await worker.registerTaskDef();
await workflow.registerWfSpec(config);
console.log('Run with: lhctl run checkpoint-tasks name obiwan');
await worker.start();