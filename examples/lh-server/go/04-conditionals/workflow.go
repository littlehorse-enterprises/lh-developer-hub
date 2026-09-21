package main

import "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"

func Wflogic(wf *littlehorse.WorkflowThread) {
	userID := wf.DeclareStr("user-id").Required()
	message := wf.DeclareStr("message").Required()
	preferredContact := wf.DeclareStr("contact-method")

	preferredContact.Assign(wf.Execute("fetch-contact-method", userID))
	wf.DoIfElse(
		preferredContact.IsEqualTo("COMLINK"),
		func(ifHandler *littlehorse.WorkflowThread) {
			ifHandler.Execute("send-comlink-message", userID, message)
		},
		func(elseHandler *littlehorse.WorkflowThread) {
			elseHandler.Execute("send-hologram", userID, message)
		},
	)
}
