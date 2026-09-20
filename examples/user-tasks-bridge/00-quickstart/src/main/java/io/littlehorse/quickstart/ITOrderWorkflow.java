package io.littlehorse.quickstart;

import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

public class ITOrderWorkflow {

    // There's a user `my-user` created automatically with the demo User Tasks Bridge
    // image. We'll just assign our task to that user. The demo image
    //
    public static final String USER_ID = "someemailaddress@somedomain.com";

    public void wfLogic(WorkflowThread wf) {
        WfRunVariable item = wf.declareStr("item").required();
        WfRunVariable employee = wf.declareStr("employee").required();
        WfRunVariable isApproved = wf.declareBool("is-approved");

        NodeOutput result = wf.assignUserTask("approve-it-rental", USER_ID, null);
        isApproved.assign(result.jsonPath("$.isApproved"));

        wf.doIf(isApproved.isEqualTo(false), ifHandler -> {
                    ifHandler.execute("decline-order", item, employee);
                })
                .doElse(elseHandler -> {
                    elseHandler.execute("ship-item", item);
                });
    }

    public Workflow getWorkflow() {
        return Workflow.newWorkflow("it-request", this::wfLogic);
    }
}
