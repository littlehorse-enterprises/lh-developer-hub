package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

func Wflogic(wf *littlehorse.WorkflowThread) {
	userId := wf.DeclareStr("user-id").Required().Searchable()
	userObject := wf.DeclareJsonObj("user-obj")
	age := wf.DeclareInt("age")
	userOutput := wf.Execute("fetch-user", userId)

	userObject.Assign(userOutput)
	age.Assign(userOutput.JsonPath("$.age"))
	message := wf.Format(
		"Hello there, {0}! You are {1} years old",
		userId, age,
	)
	wf.Execute("send-email", userObject.JsonPath("$.email"), message)
}
