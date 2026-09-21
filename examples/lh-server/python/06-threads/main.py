import asyncio
import sys

import littlehorse
from littlehorse.config import LHConfig
from littlehorse.worker import LHTaskWorker
from tasks import my_task
from workflow import MyWorkflow


def register(config: LHConfig) -> None:
    littlehorse.create_task_def(my_task, "my-task", config)
    littlehorse.create_workflow_spec(MyWorkflow().get_workflow(), config)


async def workers(config: LHConfig) -> None:
    await littlehorse.start(LHTaskWorker(my_task, "my-task", config))


if __name__ == "__main__":
    config = LHConfig()
    if len(sys.argv) != 2 or sys.argv[1] not in {"register", "workers"}:
        raise SystemExit("Usage: main.py [register|workers]")
    register(config) if sys.argv[1] == "register" else asyncio.run(workers(config))
