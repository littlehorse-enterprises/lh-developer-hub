import {
  createTaskWorker,
  LHConfig,
  spawnedThreadsOf,
  WorkerContext,
  Workflow,
} from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

function myTask(input: string, context: WorkerContext): string {
  const threadRunNumber = context.getNodeRunId()?.threadRunNumber ?? 0;
  const threadName = threadRunNumber === 0 ? "parent" : "child";
  const result = `Hello from the ${threadName} thread: ${input}`;
  console.log(result);
  return result;
}

const config = LHConfig.from({});
const worker = createTaskWorker(myTask, "my-task", config, {
  inputVars: { input: z.string() },
  outputSchema: z.string(),
});

const workflow = Workflow.newWorkflow("threads-example", (wf) => {
  const parentVariable = wf
    .declareStr("parent-var")
    .withDefault("This is the parent variable's initial value");
  wf.execute("my-task", parentVariable);

  const child = wf.spawnThread(
    (childThread) => {
      const childInput = childThread.declareStr("child-input").required();
      childThread.execute("my-task", childInput);
      childThread.execute("my-task", parentVariable);
      parentVariable.assign("This is the value of the parent variable set by the child.");
      childThread.sleepSeconds(45);
    },
    "child",
    { "child-input": "This is the input to the child thread" },
  );

  wf.sleepSeconds(25);
  wf.execute("my-task", parentVariable);
  wf.waitForThreads(spawnedThreadsOf(child));
});

await closeOnShutdown([worker]);
await worker.registerTaskDef();
await workflow.registerWfSpec(config);
console.log("Run with: lhctl run threads-example");
await worker.start();