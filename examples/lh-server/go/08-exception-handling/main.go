package main

import (
	"github.com/littlehorse-enterprises/lh-developer-hub/examples/lh-server/go/internal/examplesupport"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func main() {
	config := littlehorse.NewConfigFromEnv()
	workers := []*littlehorse.LHTaskWorker{
		examplesupport.NewWorker(config, ChargeCreditCard, "charge-credit-card"),
		examplesupport.NewWorker(config, ShipItem, "ship-item"),
		examplesupport.NewWorker(config, CancelOrderInsufficientFunds, "cancel-order-insufficient-funds"),
		examplesupport.NewWorker(config, NotifyOrderFailed, "notify-order-failed"),
	}
	workflow := littlehorse.NewWorkflow(Wflogic, "exception-example")
	examplesupport.Start(config, workers, workflow)
}
