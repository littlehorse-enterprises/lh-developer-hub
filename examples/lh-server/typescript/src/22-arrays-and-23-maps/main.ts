import { arrayOf, createTaskWorker, LHConfig, Workflow } from "littlehorse-client";
import { VariableType } from "littlehorse-client/proto";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

function processItem(item: number): void {
  console.log(`Processed ${item}`);
}

function reportResult(result: string): void {
  console.log(result);
}

const config = LHConfig.from({});
const workers = [
  createTaskWorker(processItem, "process-item", config, {
    inputVars: { item: z.number().int() },
  }),
  createTaskWorker(reportResult, "report-collection-result", config, {
    inputVars: { result: z.string() },
  }),
];

const workflow = Workflow.newWorkflow("arrays-and-maps", (wf) => {
  const numbers = wf.declareArray("my-array", VariableType.INT).required();
  const valueToCheck = wf.declareInt("value-to-check").required();
  const arraySize = wf.declareInt("array-size");

  numbers.assign(numbers.extend(4));
  numbers.assign(numbers.removeIfPresent(2));
  numbers.assign(numbers.removeIndex(1));
  arraySize.assign(numbers.size());
  wf.execute("process-item", numbers.get(0));

  wf.doIfElse(
    numbers.doesContain(valueToCheck),
    (found) => found.execute("report-collection-result", "value found"),
    (notFound) => notFound.execute("report-collection-result", "value not found"),
  );

  const children = wf.spawnThreadForEach(numbers, "process-element", (child) => {
    const input = child.declareInt("INPUT").required();
    child.execute("process-item", input);
  });
  wf.waitForThreads(children);

  const myMap = wf.declareMap(
    "my-map",
    VariableType.STR,
    VariableType.INT,
  );

  const myMapOfArrays = wf.declareMap(
    "map-of-arrays",
    VariableType.STR,
    arrayOf(VariableType.INT),
  );
});

await closeOnShutdown(workers);
await Promise.all(workers.map((worker) => worker.registerTaskDef()));
await workflow.registerWfSpec(config);
console.log(
  "Run with: lhctl run arrays-and-maps my-array '[1,2,3]' value-to-check 3 my-map '{\"one\":1}'",
);
await Promise.all(workers.map((worker) => worker.start()));