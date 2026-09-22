package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

func wfLogic(wf *littlehorse.WorkflowThread) {
	userId := wf.DeclareStr("user-id").Searchable().Required()
	userTaskDefName := "report-favorite-player"
	wf.AssignUserTask(userTaskDefName, userId, nil)

	wf.Execute("report-favorite-player", userId)
}
