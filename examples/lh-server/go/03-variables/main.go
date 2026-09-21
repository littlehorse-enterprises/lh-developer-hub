package main

import (
	"github.com/littlehorse-enterprises/lh-developer-hub/examples/lh-server/go/internal/examplesupport"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func main() {
	config := littlehorse.NewConfigFromEnv()
	workers := []*littlehorse.LHTaskWorker{
		examplesupport.NewWorker(config, FetchUser, "fetch-user"),
		examplesupport.NewWorker(config, SendEmail, "send-email"),
	}
	workflow := littlehorse.NewWorkflow(Wflogic, "variables-example")
	examplesupport.Start(config, workers, workflow)
}
