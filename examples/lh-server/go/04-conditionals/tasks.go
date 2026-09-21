package main

import "fmt"

func FetchUser(userID string) string {
	if userID == "obiwan" || userID == "padme" || userID == "satine" {
		return "COMLINK"
	}
	return "HOLOGRAM"
}

func SendComLink(userID, message string) string {
	return reportMessage("comlink", userID, message)
}

func SendHologram(userID, message string) string {
	return reportMessage("hologram", userID, message)
}

func reportMessage(method, userID, message string) string {
	result := fmt.Sprintf("sent %s %s to user %s", method, message, userID)
	fmt.Println(result)
	return result
}
