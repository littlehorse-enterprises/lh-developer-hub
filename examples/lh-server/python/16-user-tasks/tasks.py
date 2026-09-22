async def report_favorite_player(user: str, team: str, player: int) -> str:
    result = f"{user}'s favorite player is #{player} on the {team} team"
    print(result)
    return result
