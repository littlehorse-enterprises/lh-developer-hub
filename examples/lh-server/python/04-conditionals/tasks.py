async def fetch_user(user_id: str) -> str:
    return "COMLINK" if user_id in ["obiwan", "padme", "satine"] else "HOLOGRAM"


async def send_comlink(user_id: str, message: str) -> str:
    result = "sent comlink " + message + " to user " + user_id
    print(result)
    return result


async def send_hologram(user_id: str, message: str) -> str:
    result = "sent hologram " + message + " to user " + user_id
    print(result)
    return result
