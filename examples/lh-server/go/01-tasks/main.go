package main

import (
	"github.com/littlehorse-enterprises/lh-developer-hub/examples/lh-server/go/internal/examplesupport"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func main() {
	config := littlehorse.NewConfigFromEnv()
	worker := examplesupport.NewWorker(config, Greet, TaskDefName)
	examplesupport.Start(config, []*littlehorse.LHTaskWorker{worker})
}
