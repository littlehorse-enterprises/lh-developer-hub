import {
  buildPutStructDefRequest,
  createTaskWorker,
  LHConfig,
  Workflow,
} from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";
import { Car } from "./schemas.js";

function describeCar(car: z.infer<typeof Car>): string {
  return `You drive a ${car.make} ${car.model}`;
}

const config = LHConfig.from({});
const client = config.getClient();
const worker = createTaskWorker(describeCar, "describe-car", config, {
  inputVars: { car: Car },
  outputSchema: z.string(),
});

await client.putStructDef(buildPutStructDefRequest(Car));

const workflow = Workflow.newWorkflow("quickstart", (wf) => {
  const inputCar = wf.declareStruct("input-car", Car).required();
  wf.execute("describe-car", inputCar);
});

await closeOnShutdown([worker]);
await worker.registerTaskDef();
await workflow.registerWfSpec(config);
console.log("Run with: npm run structdefs:run");
await worker.start();