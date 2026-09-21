import asyncio
import sys

import littlehorse
from littlehorse.config import LHConfig
from littlehorse.model import PutUserTaskDefRequest, UserTaskField, VariableType
from littlehorse.worker import LHTaskWorker
from tasks import report_favorite_player
from workflow import get_workflow


def register(config: LHConfig) -> None:
    config.stub().PutUserTaskDef(
        PutUserTaskDefRequest(
            name="report-favorite-player",
            fields=[
                UserTaskField(
                    name="favorite-team",
                    display_name="Favorite Team",
                    type=VariableType.STR,
                    required=True,
                ),
                UserTaskField(
                    name="favorite-player-number",
                    display_name="Favorite Player's Number",
                    type=VariableType.INT,
                    required=True,
                ),
            ],
        )
    )
    littlehorse.create_task_def(
        report_favorite_player, "report-favorite-player", config
    )
    littlehorse.create_workflow_spec(get_workflow(), config)


async def workers(config: LHConfig) -> None:
    await littlehorse.start(
        LHTaskWorker(report_favorite_player, "report-favorite-player", config)
    )


if __name__ == "__main__":
    config = LHConfig()
    if len(sys.argv) != 2 or sys.argv[1] not in {"register", "workers"}:
        raise SystemExit("Usage: main.py [register|workers]")
    register(config) if sys.argv[1] == "register" else asyncio.run(workers(config))
