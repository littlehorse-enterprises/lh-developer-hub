import {
  createTaskWorker,
  LHConfig,
  LHTaskException,
  Workflow,
} from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

function chargeCreditCard(userId: string, amount: number): void {
  const currentBalance = Math.random() * 100;
  if (amount > currentBalance) {
    throw new LHTaskException(
      "insufficient-funds",
      `User ${userId} has insufficient funds`,
    );
  }
  if (Math.random() < 0.5) {
    throw new Error("Uh oh, network failure!");
  }
  console.log(`Successfully charged credit card of user ${userId}`);
}

function shipItem(itemId: string, userId: string): void {
  console.log(`Successfully shipped item ${itemId} to user ${userId}`);
}

function cancelOrderInsufficientFunds(userId: string): void {
  console.log(`Order canceled for ${userId}: insufficient funds`);
}

function notifyOrderFailed(userId: string): void {
  console.log(`Order failed for ${userId}: technical failure`);
}

const config = LHConfig.from({});
const workers = [
  createTaskWorker(chargeCreditCard, "charge-credit-card", config, {
    inputVars: { userId: z.string(), amount: z.number() },
  }),
  createTaskWorker(shipItem, "ship-item", config, {
    inputVars: { itemId: z.string(), userId: z.string() },
  }),
  createTaskWorker(
    cancelOrderInsufficientFunds,
    "cancel-order-insufficient-funds",
    config,
    { inputVars: { userId: z.string() } },
  ),
  createTaskWorker(notifyOrderFailed, "notify-order-failed", config, {
    inputVars: { userId: z.string() },
  }),
];

const workflow = Workflow.newWorkflow("exception-example", (wf) => {
  const price = wf.declareDouble("price").required();
  const itemId = wf.declareStr("item").withDefault("lightsaber");
  const userId = wf.declareStr("user-id").withDefault("obiwan");
  const charge = wf.execute("charge-credit-card", userId, price);

  wf.handleException(charge, "insufficient-funds", (handler) => {
    handler.execute("cancel-order-insufficient-funds", userId);
    handler.fail("insufficient-funds", "Credit card did not have sufficient funds");
  });
  wf.handleError(charge, null, (handler) => {
    handler.execute("notify-order-failed", userId);
    handler.fail("technical-failure", "Failed to charge credit card");
  });
  wf.execute("ship-item", itemId, userId);
});

await closeOnShutdown(workers);
await Promise.all(workers.map((worker) => worker.registerTaskDef()));
await workflow.registerWfSpec(config);
console.log("Run repeatedly with: lhctl run exception-example price 50.0");
await Promise.all(workers.map((worker) => worker.start()));