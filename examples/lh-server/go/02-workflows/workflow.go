package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

func QuickStart(wf *littlehorse.WorkflowThread) {
	name := wf.DeclareStr("name").Searchable()
	wf.Execute("greet", name)
}
