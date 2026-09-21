package main

import (
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/lhproto"
	"github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

const EventName = "document-signed"

func WfLogic(wf *littlehorse.WorkflowThread) {
	documentId := wf.DeclareStr("document-id")
	signerName := wf.WaitForEvent(EventName).
		SetCorrelationId(documentId).
		WithCorrelatedEventConfig(&lhproto.CorrelatedEventConfig{DeleteAfterFirstCorrelation: true}).
		RegisteredAs(lhproto.VariableType_STR)
	wf.Execute("processSignedDocument", documentId, signerName)
}
