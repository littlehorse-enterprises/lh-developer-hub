from typing import Any

from littlehorse.exceptions import LHTaskException


async def fetch_user(user_id: str) -> dict[str, Any]:
    if user_id == "obiwan":
        return {"email": "obiwan@jedi.temple", "title": "Master Kenobi", "age": 37}
    if user_id == "anakin":
        return {"email": "anakin@jedi.temple", "title": "Padawan Anakin", "age": 22}
    raise LHTaskException("user-not-found", "Could not find specified user")


async def send_email(to_address: str, message: str) -> str:
    result = "sent email " + message + " to address " + to_address
    print(result)
    return result
