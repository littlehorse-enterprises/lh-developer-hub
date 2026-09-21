from littlehorse.workflow import Workflow, WorkflowThread


def child_logic(wf: WorkflowThread) -> None:
    name = wf.declare_str("name").required()
    wf.complete(wf.execute("greet", name))


def parent_logic(wf: WorkflowThread) -> None:
    input_name = wf.declare_str("input-name").required()
    child_output = wf.declare_str("child-output")
    child = wf.run_wf("greeting-child", inputs={"name": input_name})
    wf.execute("greet", "hi from parent")
    child_output.assign(wf.wait_for_child_wf(child))


def get_workflows() -> tuple[Workflow, Workflow]:
    return (
        Workflow("greeting-child", child_logic),
        Workflow("greeting-parent", parent_logic),
    )
