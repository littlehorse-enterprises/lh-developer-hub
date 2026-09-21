package main

import (
	"github.com/littlehorse-enterprises/lh-developer-hub/examples/lh-server/go/internal/examplesupport"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func main() {
	config := littlehorse.NewConfigFromEnv()
	workers := []*littlehorse.LHTaskWorker{
		examplesupport.NewWorker(config, startCollection, "start-underpants-collection"),
		examplesupport.NewWorker(config, shipItem, "collect-underpant"),
		examplesupport.NewWorker(config, profit, "profit"),
	}
	workflow := littlehorse.NewWorkflow(wfLogic, "collect-underpants")
	examplesupport.Start(config, workers, workflow)
}
