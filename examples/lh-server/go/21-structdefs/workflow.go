package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

func QuickstartWorkflow(wf *littlehorse.WorkflowThread) {
	inputCar := wf.DeclareStruct("inputCar", CarStructDefName).Required()
	wf.Execute("describe-car", inputCar)
}
