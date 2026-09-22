from littlehorse.worker import WorkerContext


async def my_task(input: str, context: WorkerContext) -> str:
    thread_run_number = context.node_run_id.thread_run_number
    thread_name = "parent" if thread_run_number == 0 else "child"
    result = f"Hello from the {thread_name} thread: {input}"
    print(result)
    return result
