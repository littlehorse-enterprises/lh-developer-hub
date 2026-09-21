package io.littlehorse.docs.maps;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable scores = wf.declareMap("scores", String.class, Long.class).required();
        WfRunVariable scoresToMerge = wf.declareMap("scores-to-merge", String.class, Long.class).required();
        WfRunVariable keyToRemove = wf.declareStr("key-to-remove").required();

        scores.assign(scores.extend(scoresToMerge));
        scores.assign(scores.removeKey(keyToRemove));
        wf.declareMap("scores-by-team", String.class, Long[].class);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        Workflow workflow = Workflow.newWorkflow("maps-example", Main::wfLogic);
        workflow.registerWfSpec(config);
        System.out.println("Run with: lhctl run maps-example scores '{\"alice\":10}' scores-to-merge '{\"bob\":20}' key-to-remove alice");
    }
}