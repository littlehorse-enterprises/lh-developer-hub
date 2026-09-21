import asyncio
import sys

import littlehorse
from littlehorse.config import LHConfig
from littlehorse.worker import LHTaskWorker
from tasks import greet
from workflow import get_workflow


def register(config: LHConfig) -> None:
    littlehorse.create_task_def(greet, "greet", config)
    littlehorse.create_workflow_spec(get_workflow(), config)


async def workers(config: LHConfig) -> None:
    await littlehorse.start(LHTaskWorker(greet, "greet", config))


if __name__ == "__main__":
    config = LHConfig()
    if len(sys.argv) != 2 or sys.argv[1] not in {"register", "workers"}:
        raise SystemExit("Usage: main.py [register|workers]")
    register(config) if sys.argv[1] == "register" else asyncio.run(workers(config))
