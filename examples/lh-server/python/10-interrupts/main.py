import asyncio
import sys

import littlehorse
from littlehorse.config import LHConfig
from littlehorse.model import PutExternalEventDefRequest
from littlehorse.worker import LHTaskWorker
from tasks import profit, ship_item, start_collection
from workflow import UnderpantsWorkflow

TASKS = (
    (start_collection, "start-underpants-collection"),
    (ship_item, "collect-underpant"),
    (profit, "profit"),
)


def register(config: LHConfig) -> None:
    client = config.stub()
    client.PutExternalEventDef(PutExternalEventDefRequest(name="underpant-collected"))
    client.PutExternalEventDef(
        PutExternalEventDefRequest(name="done-collecting-underpants")
    )
    for function, name in TASKS:
        littlehorse.create_task_def(function, name, config)
    littlehorse.create_workflow_spec(UnderpantsWorkflow().get_workflow(), config)


async def workers(config: LHConfig) -> None:
    await littlehorse.start(
        *(LHTaskWorker(function, name, config) for function, name in TASKS)
    )


if __name__ == "__main__":
    config = LHConfig()
    if len(sys.argv) != 2 or sys.argv[1] not in {"register", "workers"}:
        raise SystemExit("Usage: main.py [register|workers]")
    register(config) if sys.argv[1] == "register" else asyncio.run(workers(config))
