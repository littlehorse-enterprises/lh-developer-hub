package io.littlehorse.docs.correlatedevents;

import java.util.List;

import io.littlehorse.docs.ExampleSupport;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.CorrelatedEventConfig;
import io.littlehorse.sdk.wfsdk.ExternalEventNodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.littlehorse.sdk.worker.LHTaskWorker;

public class Main {

    public static void wfLogic(WorkflowThread wf) {
        WfRunVariable documentId = wf.declareStr("document-id");
        ExternalEventNodeOutput signerName = wf.waitForEvent("document-signed")
                .withCorrelationId(documentId, true)
                .withCorrelatedEventConfig(CorrelatedEventConfig.newBuilder()
                        .setDeleteAfterFirstCorrelation(true)
                        .build())
                .registeredAs(String.class);
        wf.execute("processSignedDocument", documentId, signerName);
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        DocumentTasks tasks = new DocumentTasks();
        LHTaskWorker worker = new LHTaskWorker(tasks, "processSignedDocument", config);
        Workflow workflow = Workflow.newWorkflow("correlated-event-example", Main::wfLogic);

        System.out.println("Run with: lhctl run correlated-event-example document-id my-document-abc123");
        System.out.println("Then post a document-signed correlated event with key my-document-abc123");
        ExampleSupport.start(config, List.of(worker), workflow);
    }
}