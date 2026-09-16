import {
  createTaskWorker,
  LHConfig,
  LHTaskException,
  Workflow,
} from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

const userSchema = z.object({
  email: z.string(),
  title: z.string(),
  age: z.number().int(),
});

function fetchUser(userId: string): z.infer<typeof userSchema> {
  if (userId === "obiwan") {
    return { email: "obiwan@jedi.temple", title: "Master Kenobi", age: 37 };
  }
  if (userId === "anakin") {
    return {
      email: "anakin@jedi.temple",
      title: "Padawan Skywalker (not Master)",
      age: 22,
    };
  }
  throw new LHTaskException("user-not-found", "Could not find specified user");
}

function sendEmail(toAddress: string, message: string): string {
  const result = `sent email ${message} to address ${toAddress}`;
  console.log(result);
  return result;
}

const config = LHConfig.from({});
const workers = [
  createTaskWorker(fetchUser, "fetch-user", config, {
    inputVars: { userId: z.string() },
    outputSchema: userSchema,
  }),
  createTaskWorker(sendEmail, "send-email", config, {
    inputVars: { toAddress: z.string(), message: z.string() },
    outputSchema: z.string(),
  }),
];

const workflow = Workflow.newWorkflow("variables-example", (wf) => {
  const userId = wf.declareStr("user-id").required().searchable();
  const userObject = wf.declareJsonObj("user-obj");
  const age = wf.declareInt("age");

  // This task returns a JSON object.
  const userOutput = wf.execute("fetch-user", userId);
  userObject.assign(userOutput);

  // A field from the task output can be assigned to a non-JSON variable.
  age.assign(userOutput.jsonPath("$.age"));

  const message = wf.format(
    "Hello there, {0}! You are {1} years old",
    userObject.jsonPath("$.title"),
    age,
  );
  wf.execute("send-email", userObject.jsonPath("$.email"), message);
});

await closeOnShutdown(workers);
await Promise.all(workers.map((worker) => worker.registerTaskDef()));
await workflow.registerWfSpec(config);
console.log('Run with: lhctl run variables-example user-id obiwan');
await Promise.all(workers.map((worker) => worker.start()));