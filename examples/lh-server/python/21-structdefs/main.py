import asyncio
import sys

import littlehorse
from littlehorse.config import LHConfig
from littlehorse.worker import LHTaskWorker
from tasks import Car, describe_car
from workflow import quickstart_wf


def register(config: LHConfig) -> None:
    littlehorse.create_struct_def(Car, config)
    littlehorse.create_task_def(describe_car, "describe-car", config)
    littlehorse.create_workflow_spec(quickstart_wf(), config)


async def workers(config: LHConfig) -> None:
    await littlehorse.start(LHTaskWorker(describe_car, "describe-car", config))


if __name__ == "__main__":
    config = LHConfig()
    if len(sys.argv) != 2 or sys.argv[1] not in {"register", "workers"}:
        raise SystemExit("Usage: main.py [register|workers]")
    register(config) if sys.argv[1] == "register" else asyncio.run(workers(config))
