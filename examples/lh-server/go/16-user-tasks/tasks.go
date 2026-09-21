package main

import "fmt"

func reportFavoritePlayer(user, team string, player int) string {
	result := fmt.Sprintf("%s's favorite player is #%d on the %s team!", user, player, team)
	fmt.Println(result)
	return result
}
