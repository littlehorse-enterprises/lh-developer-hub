from typing import Any


async def start_collection() -> None:
    print("starting-collection-of-underpants")


async def ship_item(underpant_owner: str) -> str:
    result = f"Successfully collected underpant from {underpant_owner}"
    print(result)
    return result


async def profit(underpants: list[Any]) -> str:
    result = f"Collected {len(underpants)} underpants!"
    print(result)
    return result
