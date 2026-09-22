from littlehorse.workflow import Workflow, WorkflowThread


def get_workflow() -> Workflow:
    def quickstart_workflow(wf: WorkflowThread) -> None:
        name = wf.declare_str("name").searchable()
        wf.execute("greet", name)

    return Workflow("quickstart", quickstart_workflow)
