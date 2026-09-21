import asyncio
import sys

import littlehorse
from littlehorse.config import LHConfig
from littlehorse.worker import LHTaskWorker
from tasks import (
    cancel_order_insufficient_funds,
    charge_credit_card,
    fetch_amount,
    notify_order_failed,
    ship_item,
)
from workflow import get_workflow

TASKS = (
    (charge_credit_card, "charge-credit-card"),
    (fetch_amount, "fetch-amount"),
    (ship_item, "ship-item"),
    (cancel_order_insufficient_funds, "cancel-order-insufficient-funds"),
    (notify_order_failed, "notify-order-failed"),
)


def register(config: LHConfig) -> None:
    for function, name in TASKS:
        littlehorse.create_task_def(function, name, config)
    littlehorse.create_workflow_spec(get_workflow(), config)


async def workers(config: LHConfig) -> None:
    await littlehorse.start(
        *(LHTaskWorker(function, name, config) for function, name in TASKS)
    )


if __name__ == "__main__":
    config = LHConfig()
    if len(sys.argv) != 2 or sys.argv[1] not in {"register", "workers"}:
        raise SystemExit("Usage: main.py [register|workers]")
    register(config) if sys.argv[1] == "register" else asyncio.run(workers(config))
