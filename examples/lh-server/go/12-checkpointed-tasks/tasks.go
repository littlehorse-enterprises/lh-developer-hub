package main

import (
	"fmt"

	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func Greeting(name string, context *littlehorse.WorkerContext) (string, error) {
	attemptNumber := context.GetAttemptNumber()
	fmt.Printf("metadata: wfRun=%s taskRun=%s attempt=%d\n",
		context.GetWfRunId().GetId(), context.ScheduledTask.TaskRunId.GetTaskGuid(), attemptNumber)

	sideEffectReceipt, err := context.ExecuteAndCheckpoint(func(checkpointContext *littlehorse.CheckpointContext) (interface{}, error) {
		checkpointContext.Log("sending the greeting side effect")
		fmt.Println("SIDE EFFECT: send greeting for " + name)
		return "receipt-for-" + name, nil
	})
	if err != nil {
		return "", err
	}

	fmt.Println("after checkpoint on attempt", attemptNumber)
	if attemptNumber == 0 {
		return "", fmt.Errorf("Intentional post-checkpoint failure on the first attempt.")
	}

	return fmt.Sprintf("%s; completed on attempt %d", sideEffectReceipt.(string), attemptNumber), nil
}
