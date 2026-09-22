package main

import (
	"github.com/littlehorse-enterprises/lh-developer-hub/examples/lh-server/go/internal/examplesupport"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func main() {
	config := littlehorse.NewConfigFromEnv()
	worker := examplesupport.NewWorker(config, ProcessSignedDocument, "processSignedDocument")
	workflow := littlehorse.NewWorkflow(WfLogic, "correlated-event-example")
	examplesupport.Start(config, []*littlehorse.LHTaskWorker{worker}, workflow)
}
