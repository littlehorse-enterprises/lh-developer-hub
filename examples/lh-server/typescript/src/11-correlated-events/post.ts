import { config } from "../config.js";

const result = await config.getClient().putCorrelatedEvent({
  key: "my-document-abc123",
  externalEventDefId: { name: "document-signed" },
  content: { value: { oneofKind: "str", str: "Obi-Wan Kenobi" } },
});
console.log(result);