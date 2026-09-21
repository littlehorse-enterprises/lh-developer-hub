import asyncio
import sys

import littlehorse
from littlehorse.config import LHConfig
from littlehorse.worker import LHTaskWorker
from tasks import fetch_user, send_comlink, send_hologram
from workflow import get_workflow


def register(config: LHConfig) -> None:
    littlehorse.create_task_def(fetch_user, "fetch-user", config)
    littlehorse.create_task_def(send_comlink, "send-comlink", config)
    littlehorse.create_task_def(send_hologram, "send-hologram", config)
    littlehorse.create_workflow_spec(get_workflow(), config)


async def workers(config: LHConfig) -> None:
    await littlehorse.start(
        LHTaskWorker(fetch_user, "fetch-user", config),
        LHTaskWorker(send_comlink, "send-comlink", config),
        LHTaskWorker(send_hologram, "send-hologram", config),
    )


if __name__ == "__main__":
    config = LHConfig()
    if len(sys.argv) != 2 or sys.argv[1] not in {"register", "workers"}:
        raise SystemExit("Usage: main.py [register|workers]")
    register(config) if sys.argv[1] == "register" else asyncio.run(workers(config))
