import random

from littlehorse.exceptions import LHTaskException


async def charge_credit_card(user_id: str, amount: float) -> None:
    if amount > 10000:
        raise LHTaskException(
            "insufficient-funds", f"User {user_id} has insufficient funds"
        )
    if random.randint(0, 1):
        raise RuntimeError("Uh oh, network failure!")
    print(f"Successfully charged credit card of user {user_id}")


async def fetch_amount(user_id: str) -> float:
    return random.uniform(0.0, 100.0)


async def ship_item(item_id: str, user_id: str) -> None:
    print(f"Successfully shipped item {item_id} to user {user_id}")


async def cancel_order_insufficient_funds(user_id: str) -> None:
    print(
        f"Notifying user {user_id} that order was cancelled due to insufficient funds"
    )


async def notify_order_failed(user_id: str) -> None:
    print(f"Notifying user {user_id} that order failed for technical reasons")
