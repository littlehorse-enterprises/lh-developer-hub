from littlehorse.worker import CheckpointContext, WorkerContext


async def greeting(name: str, context: WorkerContext) -> str:
    attempt_number = context.attempt_number
    print(
        f"metadata: wfRun={context.wf_run_id.id} "
        f"taskRun={context.task_run_id.task_guid} attempt={attempt_number}"
    )

    def create_greeting(checkpoint_context: CheckpointContext) -> str:
        checkpoint_context.log("sending the greeting side effect")
        print("SIDE EFFECT: send greeting for " + name)
        return "receipt-for-" + name

    side_effect_receipt = await context.execute_and_checkpoint(create_greeting)

    print("after checkpoint on attempt " + str(attempt_number))
    if attempt_number == 0:
        raise RuntimeError("Intentional post-checkpoint failure on the first attempt.")

    return side_effect_receipt + "; completed on attempt " + str(attempt_number)