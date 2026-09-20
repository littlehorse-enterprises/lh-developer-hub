package main

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/littlehorse-enterprises/littlehorse/sdk-go/lhproto"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func main() {
	if len(os.Args) != 2 || (os.Args[1] != "register" && os.Args[1] != "workers") {
		fmt.Fprintln(os.Stderr, "Please provide one argument: either 'register' or 'workers'")
		os.Exit(1)
	}

	config := littlehorse.NewConfigFromEnv()
	workers := createWorkers(config)
	if os.Args[1] == "register" {
		registerMetadata(config, workers)
	} else {
		startWorkers(workers)
	}
}

func createWorkers(config *littlehorse.LHConfig) []*littlehorse.LHTaskWorker {
	workers := make([]*littlehorse.LHTaskWorker, 0, 3)
	for _, definition := range []struct {
		function any
		name     string
	}{
		{VerifyIdentity, verifyIdentityTask},
		{NotifyCustomerVerified, notifyVerifiedTask},
		{NotifyCustomerNotVerified, notifyNotVerifiedTask},
	} {
		worker, err := littlehorse.NewTaskWorker(config, definition.function, definition.name)
		if err != nil {
			log.Fatal(err)
		}
		workers = append(workers, worker)
	}
	return workers
}

func registerMetadata(config *littlehorse.LHConfig, workers []*littlehorse.LHTaskWorker) {
	client, err := config.GetGrpcClient()
	if err != nil {
		log.Fatal(err)
	}

	_, err = (*client).PutExternalEventDef(context.Background(), &lhproto.PutExternalEventDefRequest{
		Name: identityVerifiedEvent,
		ContentType: &lhproto.ReturnType{ReturnType: &lhproto.TypeDefinition{
			DefinedType: &lhproto.TypeDefinition_PrimitiveType{PrimitiveType: lhproto.VariableType_BOOL},
		}},
		CorrelatedEventConfig: &lhproto.CorrelatedEventConfig{DeleteAfterFirstCorrelation: false},
	})
	if err != nil {
		log.Fatal(err)
	}

	for _, worker := range workers {
		if err := worker.RegisterTaskDef(); err != nil {
			log.Fatal(err)
		}
	}

	workflow := littlehorse.NewWorkflow(QuickstartWorkflow, wfSpecName)
	request, err := workflow.Compile()
	if err != nil {
		log.Fatal(err)
	}
	if _, err = (*client).PutWfSpec(context.Background(), request); err != nil {
		log.Fatal(err)
	}
	log.Printf("Registered TaskDefs, ExternalEventDef, and WfSpec %s", wfSpecName)
}

func startWorkers(workers []*littlehorse.LHTaskWorker) {
	defer func() {
		for _, worker := range workers {
			worker.Close()
		}
	}()

	for _, worker := range workers {
		go func(worker *littlehorse.LHTaskWorker) {
			if err := worker.Start(); err != nil {
				log.Print(err)
			}
		}(worker)
	}
	log.Print("Task workers started. Press Ctrl+C to stop.")
	select {}
}
