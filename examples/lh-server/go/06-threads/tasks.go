package main

import (
	"fmt"

	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func MyTask(input string, context *littlehorse.WorkerContext) string {
	threadName := "child"
	if context.GetNodeRunId().GetThreadRunNumber() == 0 {
		threadName = "parent"
	}
	result := "Hello from the " + threadName + " thread: " + input
	fmt.Println(result)
	return result
}
