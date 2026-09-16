import { createTaskWorker } from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown, config } from "../config.js";

function greet(name: string): string {
  const result = `Hello there, ${name}!`;
  console.log(result);
  return result;
}

const worker = createTaskWorker(greet, "greet", config, {
  inputVars: { name: z.string() },
  outputSchema: z.string(),
});

await closeOnShutdown([worker]);
await worker.registerTaskDef();
await worker.start();