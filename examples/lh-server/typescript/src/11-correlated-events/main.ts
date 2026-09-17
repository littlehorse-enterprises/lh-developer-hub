import { createTaskWorker, LHConfig, Workflow } from "littlehorse-client";
import { z } from "zod";
import { closeOnShutdown } from "../config.js";

function processSignedDocument(documentId: string, signerName: string): string {
  const result = `Document ${documentId} was signed by ${signerName}. Processing complete.`;
  console.log(result);
  return result;
}

const config = LHConfig.from({});
const worker = createTaskWorker(
  processSignedDocument,
  "processSignedDocument",
  config,
  {
    inputVars: { documentId: z.string(), signerName: z.string() },
    outputSchema: z.string(),
  },
);

const workflow = Workflow.newWorkflow("correlated-event-example", (wf) => {
  const documentId = wf.declareStr("document-id");
  const signerName = wf.waitForEvent("document-signed", {
    correlationId: documentId,
    correlatedEventConfig: { deleteAfterFirstCorrelation: true },
    payloadSchema: z.string(),
  });
  wf.execute("processSignedDocument", documentId, signerName);
});

await closeOnShutdown([worker]);
await worker.registerTaskDef();
await workflow.registerWfSpec(config);
console.log("Run with: lhctl run correlated-event-example document-id my-document-abc123");
console.log("Then post with: npm run correlated-events:post");
await worker.start();