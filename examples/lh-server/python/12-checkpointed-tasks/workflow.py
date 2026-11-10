from littlehorse.workflow import Workflow, WorkflowThread


def get_workflow() -> Workflow:
    def wf_logic(wf: WorkflowThread) -> None:
        name = wf.declare_str("name").required()
        wf.execute("checkpoint-greet-python", name, retries=2)

    return Workflow("checkpoint-tasks", wf_logic)