package io.littlehorse.examples;

import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.usertask.UserTaskSchema;
import io.littlehorse.sdk.wfsdk.UserTaskOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;
import io.littlehorse.sdk.worker.LHTaskWorker;
import java.time.Duration;

public final class UserTasksExample {

    private static final String WF_NAME = "purchase-approval";
    private static final String REQUEST_FORM = "purchase-request-form";
    private static final String APPROVAL_FORM = "purchase-approval-form";
    private static final String REQUESTER_GROUP = "it-support";
    private static final String FINANCE_GROUP = "finance";
    private static final String REQUEST_CANCELLED = "purchase-request-cancelled";
    private static final String APPROVAL_CANCELLED = "purchase-approval-cancelled";

    private UserTasksExample() {}

    private static Workflow configure(Workflow workflow) {
        workflow.setDefaultTaskRetries(1);
        return workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                .setSecondsAfterWfTermination(Duration.ofDays(14).toSeconds())
                .build());
    }

    public static Workflow workflow() {
        return configure(Workflow.newWorkflow(WF_NAME, wf -> {
            WfRunVariable userId = wf.declareStr("user-id").required();
            WfRunVariable request = wf.declareJsonObj("request");
            WfRunVariable approved = wf.declareBool("approved");

            UserTaskOutput requestTask = wf.assignUserTask(REQUEST_FORM, userId, REQUESTER_GROUP);
            wf.releaseToGroupOnDeadline(requestTask, 60);
            requestTask.withOnCancellationException(REQUEST_CANCELLED);
            wf.handleException(requestTask, REQUEST_CANCELLED, handler -> handler.execute(
                    NotificationTasks.SEND_EMAIL_TASK,
                    userId,
                    "The purchase request was cancelled."));
            request.assign(requestTask);

            UserTaskOutput approvalTask = wf.assignUserTask(APPROVAL_FORM, null, FINANCE_GROUP)
                    .withNotes(wf.format(
                            "User {0} requested {1}: {2}",
                            userId,
                            request.jsonPath("$.requestedItem"),
                            request.jsonPath("$.justification")))
                    .withOnCancellationException(APPROVAL_CANCELLED);
            wf.scheduleReminderTask(
                    approvalTask,
                    5,
                    NotificationTasks.SEND_EMAIL_TASK,
                    "finance@example.com",
                    "A purchase request is waiting for approval.");
            wf.reassignUserTask(approvalTask, "finance-backup", null, 10);
            wf.cancelUserTaskRunAfter(approvalTask, 30);
            wf.handleException(approvalTask, APPROVAL_CANCELLED, handler -> handler.execute(
                    NotificationTasks.SEND_EMAIL_TASK,
                    userId,
                    "The purchase approval expired or was cancelled."));
            approved.assign(approvalTask.jsonPath("$.approved"));

            wf.doIf(
                            approved.isEqualTo(true),
                            yes -> yes.execute(
                                    NotificationTasks.SEND_EMAIL_TASK,
                                    userId,
                                    wf.format("Approved: {0}", request.jsonPath("$.requestedItem"))))
                    .doElse(no -> no.execute(
                            NotificationTasks.SEND_EMAIL_TASK,
                            userId,
                            wf.format("Denied: {0}", request.jsonPath("$.requestedItem"))));
            wf.complete();
        }));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub client = config.getBlockingStub();

        LHTaskWorker worker = new LHTaskWorker(
                new NotificationTasks(), NotificationTasks.SEND_EMAIL_TASK, config);
        worker.registerTaskDef();

        // UserTaskDefs must exist before the WfSpec references their forms.
        client.putUserTaskDef(new UserTaskSchema(new ItemRequestForm(), REQUEST_FORM).compile());
        client.putUserTaskDef(new UserTaskSchema(new ApprovalForm(), APPROVAL_FORM).compile());
        workflow().registerWfSpec(config);

        Runtime.getRuntime().addShutdownHook(new Thread(worker::close));
        worker.start();

        System.out.println("Registered purchase-approval and both user-task forms.");
        System.out.println("Start a run with user-id=anakin, then find its tasks with lhctl.");
    }
}
