import { LHStatus, UserTaskRunId, WfRunId } from "littlehorse-client/proto";
import { config } from "../config.js";

const client = config.getClient();

async function findUserTask(wfRunId: string): Promise<UserTaskRunId> {
  for (let attempt = 0; attempt < 50; attempt++) {
    const found = await client.searchUserTaskRun({
      userTaskDefName: "report-favorite-player",
      userId: "obiwan",
    });
    const result = found.results.find((id) => id.wfRunId?.id === wfRunId);
    if (result) return result;
    await new Promise((resolve) => setTimeout(resolve, 200));
  }
  throw new Error("UserTaskRun did not appear");
}

async function waitForRun(id: WfRunId): Promise<void> {
  for (let attempt = 0; attempt < 100; attempt++) {
    const run = await client.getWfRun(id);
    if (run.status === LHStatus.COMPLETED) return;
    if (run.status === LHStatus.ERROR || run.status === LHStatus.EXCEPTION) {
      throw new Error(`WfRun finished as ${LHStatus[run.status]}`);
    }
    await new Promise((resolve) => setTimeout(resolve, 200));
  }
  throw new Error("WfRun did not finish");
}

const run = await client.runWf({
  wfSpecName: "favorite-player-demo",
  variables: { "user-id": { value: { oneofKind: "str", str: "obiwan" } } },
});
const userTaskRunId = await findUserTask(run.id!.id);
await client.completeUserTaskRun({
  userTaskRunId,
  userId: "obiwan",
  results: {
    favoriteTeam: { value: { oneofKind: "str", str: "San Jose Sharks" } },
    favoritePlayerNumber: { value: { oneofKind: "int", int: "19" } },
  },
});
await waitForRun(run.id!);
console.log(`Completed ${run.id!.id}`);