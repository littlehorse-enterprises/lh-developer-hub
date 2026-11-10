package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

func WfLogic(wf *littlehorse.WorkflowThread) {
	name := wf.DeclareStr("name").Required()
	wf.Execute("checkpoint-greet-go", name).WithRetries(2)
}
