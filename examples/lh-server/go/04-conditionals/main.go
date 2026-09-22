package main

import (
	"github.com/littlehorse-enterprises/lh-developer-hub/examples/lh-server/go/internal/examplesupport"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func main() {
	config := littlehorse.NewConfigFromEnv()
	workers := []*littlehorse.LHTaskWorker{
		examplesupport.NewWorker(config, FetchUser, "fetch-contact-method"),
		examplesupport.NewWorker(config, SendComLink, "send-comlink-message"),
		examplesupport.NewWorker(config, SendHologram, "send-hologram"),
	}
	workflow := littlehorse.NewWorkflow(Wflogic, "send-message")
	examplesupport.Start(config, workers, workflow)
}
