package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;

public final class AgentWorkflow {

    public static void main(String[] args) {
        Workflow workflow = Workflow.newWorkflow("text-to-text-example", wf -> {
            WfRunVariable input = wf.declareStr("input").required();
            WfRunVariable output = wf.declareStr("output");
            output.assign(wf.execute("text-to-text-agent", input).timeout(300));
        });
        workflow.registerWfSpec(new LHConfig().getBlockingStub());
        System.out.println("Registered WfSpec text-to-text-example");
    }
}
