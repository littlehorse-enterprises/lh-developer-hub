package io.littlehorse.docs.correlatedevents;

import io.littlehorse.sdk.worker.LHTaskMethod;

public class DocumentTasks {

    @LHTaskMethod("processSignedDocument")
    public String processSignedDocument(String documentId, String signerName) {
        String result = "Document " + documentId + " was signed by " + signerName + ". Processing complete.";
        System.out.println(result);
        return result;
    }
}