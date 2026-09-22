import { createTaskWorker, LHConfig, userTaskSchema, Workflow } from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

function reportFavoritePlayer(user: string, team: string, player: number): string {
  const result = `${user}'s favorite player is #${player} on the ${team} team!`;
  console.log(result);
  return result;
}

const config = LHConfig.from({});
const worker = createTaskWorker(
  reportFavoritePlayer,
  "report-favorite-player",
  config,
  {
    inputVars: { user: z.string(), team: z.string(), player: z.number().int() },
    outputSchema: z.string(),
  },
);

await userTaskSchema("report-favorite-player", {
  favoriteTeam: {
    schema: z.string(),
    displayName: "Favorite Team",
    required: true,
  },
  favoritePlayerNumber: {
    schema: z.number().int(),
    displayName: "Favorite Player's Number",
    required: true,
  },
}).register(config);

const workflow = Workflow.newWorkflow("favorite-player-demo", (wf) => {
  const userId = wf.declareStr("user-id").searchable().required();
  const favoriteTeam = wf.declareStr("favorite-team");
  const favoriteNumber = wf.declareInt("favorite-player-number");
  const formResult = wf.assignUserTask("report-favorite-player", userId, null);

  favoriteTeam.assign(formResult.get("favoriteTeam"));
  favoriteNumber.assign(formResult.get("favoritePlayerNumber"));
  wf.execute("report-favorite-player", userId, favoriteTeam, favoriteNumber);
});

await closeOnShutdown([worker]);
await worker.registerTaskDef();
await workflow.registerWfSpec(config);
console.log("Run with: lhctl run favorite-player-demo user-id obiwan");
console.log(
  "Then search with: lhctl search userTaskRun --userTaskDefName report-favorite-player --userId obiwan",
);
console.log("Complete with: lhctl execute userTaskRun <WfRunId> <Guid>");
await worker.start();