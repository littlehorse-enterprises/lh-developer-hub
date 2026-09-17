import { arrayOf, LHConfig, Workflow } from "littlehorse-client";
import { VariableType } from "littlehorse-client/proto";

const config = LHConfig.from({});

const workflow = Workflow.newWorkflow("maps-example", (wf) => {
  const scores = wf
    .declareMap("scores", VariableType.STR, VariableType.INT)
    .required();
  const scoresToMerge = wf
    .declareMap("scores-to-merge", VariableType.STR, VariableType.INT)
    .required();
  const keyToRemove = wf.declareStr("key-to-remove").required();

  scores.assign(scores.extend(scoresToMerge));
  scores.assign(scores.removeKey(keyToRemove));

  wf.declareMap(
    "scores-by-team",
    VariableType.STR,
    arrayOf(VariableType.INT),
  );
});

await workflow.registerWfSpec(config);
console.log(
  "Run with: lhctl run maps-example scores '{\"alice\":10}' scores-to-merge '{\"bob\":20}' key-to-remove alice",
);
