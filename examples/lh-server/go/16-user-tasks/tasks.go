package main

import "fmt"

func reportFavoritePlayer(user string) string {
	result := fmt.Sprintf("%s completed the favorite-player report", user)
	fmt.Println(result)
	return result
}
