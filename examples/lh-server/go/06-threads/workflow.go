package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

var parentVariable *littlehorse.WfRunVariable

func EntrypointThreadLogic(wf *littlehorse.WorkflowThread) {
	parentVariable = wf.DeclareStr("parent-var").WithDefault("This is the default value of the parent variable")
	wf.Execute("my-task", parentVariable)

	child := wf.SpawnThread(Wflogic, "child", map[string]interface{}{
		"child-input": "This is the input to the child thread",
	})

	wf.Sleep(25)
	wf.Execute("my-task", parentVariable)
	wf.WaitForThreads(child)
}

func Wflogic(wf *littlehorse.WorkflowThread) {
	childInput := wf.DeclareStr("child-input").Required()
	wf.Execute("my-task", childInput)
	wf.Execute("my-task", parentVariable)
	parentVariable.Assign("This is the value of the parent variable set by the child.")
	wf.Sleep(45)
}
