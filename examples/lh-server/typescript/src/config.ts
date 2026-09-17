import { LHConfig } from "littlehorse-client";

export const config = LHConfig.from({});

export async function closeOnShutdown(workers: Array<{ close(): Promise<void> }>): Promise<void> {
  const close = async (): Promise<void> => {
    await Promise.all(workers.map((worker) => worker.close()));
  };

  process.on("SIGINT", () => void close());
  process.on("SIGTERM", () => void close());
}