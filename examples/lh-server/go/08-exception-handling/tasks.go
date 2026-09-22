package main

import (
	"fmt"
	"math/rand"

	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func ChargeCreditCard(userID string, amount float64) (string, error) {
	currentBalance := rand.Float64() * 100
	if amount > currentBalance {
		return "", &littlehorse.LHTaskException{
			Name:    "insufficient-funds",
			Message: fmt.Sprintf("User %s has insufficient funds", userID),
		}
	}
	if rand.Intn(2) == 0 {
		return "", fmt.Errorf("network failure")
	}
	fmt.Printf("Successfully charged credit card of user %s\n", userID)
	return "success", nil
}

func ShipItem(itemID, userID string) {
	fmt.Printf("Successfully shipped item %s to user %s\n", itemID, userID)
}

func CancelOrderInsufficientFunds(userID string) {
	fmt.Printf("Order canceled for %s: insufficient funds\n", userID)
}

func NotifyOrderFailed(userID string) {
	fmt.Printf("Order failed for %s: technical failure\n", userID)
}
