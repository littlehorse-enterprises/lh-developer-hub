package main

import (
	"log"

	"github.com/littlehorse-enterprises/lh-developer-hub/examples/lh-server/go/internal/examplesupport"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func main() {
	config := littlehorse.NewConfigFromEnv()
	client, err := config.GetGrpcClient()
	if err != nil {
		log.Fatal(err)
	}
	if err := littlehorse.RegisterStructDef(*client, Car{}, nil); err != nil {
		log.Fatal(err)
	}

	worker := examplesupport.NewWorker(config, DescribeCar, "describe-car")
	workflow := littlehorse.NewWorkflow(QuickstartWorkflow, "quickstart")
	examplesupport.Start(config, []*littlehorse.LHTaskWorker{worker}, workflow)
}
