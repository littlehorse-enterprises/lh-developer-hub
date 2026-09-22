package main

import (
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/lhproto"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

const ExternalEventDefName = "name-posted"

func WfLogic(wf *littlehorse.WorkflowThread) {
	name := wf.DeclareStr("name")
	eventPayLoad := wf.WaitForEvent(ExternalEventDefName).RegisteredAs(lhproto.VariableType_STR)
	name.Assign(eventPayLoad)
	wf.Execute("greet", name)
}
