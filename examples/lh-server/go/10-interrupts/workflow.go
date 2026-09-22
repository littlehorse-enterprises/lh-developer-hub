package main

import (
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/lhproto"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

var allUnderpants *littlehorse.WfRunVariable

func wfLogic(wf *littlehorse.WorkflowThread) {
	allUnderpants = wf.DeclareJsonArr("all-underpants")
	wf.Execute("start-underpants-collection")
	wf.WaitForEvent("done-collecting-underpants").RegisteredAsEmpty()
	wf.Execute("profit", allUnderpants)

	wf.HandleInterrupt("underpant-collected", handleUnderpant).RegisteredAs(lhproto.VariableType_STR)
}

func handleUnderpant(wf *littlehorse.WorkflowThread) {
	interruptContent := wf.DeclareStr("INPUT")
	allUnderpants.Assign(allUnderpants.Add(interruptContent))
	wf.Execute("collect-underpant", interruptContent)
}
