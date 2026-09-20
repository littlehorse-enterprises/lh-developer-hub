package main

import (
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/lhproto"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

const (
	wfSpecName            = "quickstart"
	identityVerifiedEvent = "identity-verified"
	verifyIdentityTask    = "verify-identity"
	notifyVerifiedTask    = "notify-customer-verified"
	notifyNotVerifiedTask = "notify-customer-not-verified"
)

func QuickstartWorkflow(wf *littlehorse.WorkflowThread) {
	fullName := wf.DeclareStr("full-name").Searchable().Required()
	email := wf.DeclareStr("email").Searchable().Required()
	ssn := wf.DeclareInt("ssn").MaskedValue().Required()
	identityVerified := wf.DeclareBool("identity-verified").Searchable()

	wf.Execute(verifyIdentityTask, fullName, email, ssn).WithRetries(3)
	verification := wf.WaitForEvent(identityVerifiedEvent).
		RegisteredAs(lhproto.VariableType_BOOL).
		Timeout(300).
		SetCorrelationId(email)

	timeout := littlehorse.Timeout
	wf.HandleError(verification, &timeout, func(handler *littlehorse.WorkflowThread) {
		handler.Execute(notifyNotVerifiedTask, fullName, email)
		message := "Unable to verify customer identity in time."
		handler.Fail(nil, "customer-not-verified", &message)
	})

	identityVerified.Assign(verification)
	wf.DoIfElse(
		identityVerified.IsEqualTo(true),
		func(yes *littlehorse.WorkflowThread) {
			yes.Execute(notifyVerifiedTask, fullName, email)
		},
		func(no *littlehorse.WorkflowThread) {
			no.Execute(notifyNotVerifiedTask, fullName, email)
		},
	)
}
