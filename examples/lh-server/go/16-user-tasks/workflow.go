package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

func wfLogic(wf *littlehorse.WorkflowThread) {
	userId := wf.DeclareStr("user-id").Searchable().Required()
	favoriteTeam := wf.DeclareStr("favorite-team")
	favoriteNumber := wf.DeclareInt("favorite-player-number")
	userTaskDefName := "report-favorite-player"
	formResult := wf.AssignUserTask(userTaskDefName, userId, nil)

	favoriteTeam.Assign(formResult.JsonPath("$.favoriteTeam"))
	favoriteNumber.Assign(formResult.JsonPath("$.favoritePlayerNumber"))
	wf.Execute("report-favorite-player", userId, favoriteTeam, favoriteNumber)
}
