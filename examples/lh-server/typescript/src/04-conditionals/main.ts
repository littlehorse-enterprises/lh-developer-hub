import { createTaskWorker, LHConfig, Workflow } from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

function fetchContactMethod(userId: string): string {
  return ["obiwan", "padme", "satine"].includes(userId)
    ? "COMLINK"
    : "HOLOGRAM";
}

function sendComlink(userId: string, message: string): string {
  const result = `sent comlink ${message} to user ${userId}`;
  console.log(result);
  return result;
}

function sendHologram(userId: string, message: string): string {
  const result = `sent hologram ${message} to user ${userId}`;
  console.log(result);
  return result;
}

const config = LHConfig.from({});
const workers = [
  createTaskWorker(fetchContactMethod, "fetch-contact-method", config, {
    inputVars: { userId: z.string() },
    outputSchema: z.string(),
  }),
  createTaskWorker(sendComlink, "send-comlink-message", config, {
    inputVars: { userId: z.string(), message: z.string() },
    outputSchema: z.string(),
  }),
  createTaskWorker(sendHologram, "send-hologram", config, {
    inputVars: { userId: z.string(), message: z.string() },
    outputSchema: z.string(),
  }),
];

const workflow = Workflow.newWorkflow("send-message", (wf) => {
  const userId = wf.declareStr("user-id").required();
  const message = wf.declareStr("message").required();
  const preferredContact = wf.declareStr("contact-method");

  preferredContact.assign(wf.execute("fetch-contact-method", userId));
  wf.doIfElse(
    preferredContact.isEqualTo("COMLINK"),
    (ifHandler) => {
      ifHandler.execute("send-comlink-message", userId, message);
    },
    (elseHandler) => {
      elseHandler.execute("send-hologram", userId, message);
    },
  );
});

await closeOnShutdown(workers);
await Promise.all(workers.map((worker) => worker.registerTaskDef()));
await workflow.registerWfSpec(config);
console.log('Run with: lhctl run send-message user-id anakin message "Hello there"');
await Promise.all(workers.map((worker) => worker.start()));