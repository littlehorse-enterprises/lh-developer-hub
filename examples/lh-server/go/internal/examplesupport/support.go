package examplesupport

import (
	"log"

	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func NewWorker(config *littlehorse.LHConfig, taskFunction any, taskDefName string) *littlehorse.LHTaskWorker {
	worker, err := littlehorse.NewTaskWorker(config, taskFunction, taskDefName)
	if err != nil {
		log.Fatal(err)
	}
	return worker
}

func Start(config *littlehorse.LHConfig, workers []*littlehorse.LHTaskWorker, workflows ...*littlehorse.LHWorkflow) {
	client, err := config.GetGrpcClient()
	if err != nil {
		log.Fatal(err)
	}

	for _, worker := range workers {
		if err := worker.RegisterTaskDef(); err != nil {
			log.Fatal(err)
		}
	}
	for _, workflow := range workflows {
		workflow.RegisterWfSpec(*client)
	}

	if len(workers) == 0 {
		return
	}
	defer func() {
		for _, worker := range workers {
			if err := worker.Close(); err != nil {
				log.Print(err)
			}
		}
	}()

	for _, worker := range workers {
		go func() {
			if err := worker.Start(); err != nil {
				log.Print(err)
			}
		}()
	}
	log.Print("Task workers started. Press Ctrl+C to stop.")
	select {}
}
