package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

func ChildWorkflow(wf *littlehorse.WorkflowThread) {
	name := wf.DeclareStr("name").Required()
	wf.Complete(wf.Execute("greet", name))
}

func ParentWorkflow(wf *littlehorse.WorkflowThread) {
	inputName := wf.DeclareStr("input-name").Required()
	childOutput := wf.DeclareStr("child-output")
	child := wf.RunWf("greeting-child", map[string]interface{}{"name": inputName})

	wf.Execute("greet", "hi from parent")
	childOutput.Assign(wf.WaitForChildWf(child))
}
