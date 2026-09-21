package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

func Wflogic(wf *littlehorse.WorkflowThread) {
	price := wf.DeclareDouble("price").Required()
	itemID := wf.DeclareStr("item").WithDefault("lightsaber")
	userID := wf.DeclareStr("user-id").WithDefault("obiwan")
	charge := wf.Execute("charge-credit-card", userID, price)

	insufficientFunds := "insufficient-funds"
	wf.HandleException(charge, &insufficientFunds, func(handler *littlehorse.WorkflowThread) {
		handler.Execute("cancel-order-insufficient-funds", userID)
		message := "Credit card did not have sufficient funds"
		handler.Fail(nil, "insufficient-funds", &message)
	})
	wf.HandleError(charge, nil, func(handler *littlehorse.WorkflowThread) {
		handler.Execute("notify-order-failed", userID)
		message := "Failed to charge credit card"
		handler.Fail(nil, "technical-failure", &message)
	})
	wf.Execute("ship-item", itemID, userID)
}
