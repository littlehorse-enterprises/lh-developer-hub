from littlehorse.workflow import Workflow, WorkflowThread
from tasks import Car


def quickstart_wf() -> Workflow:
    def my_entrypoint(wf: WorkflowThread) -> None:
        input_car = wf.declare_struct("input-car", Car).required()
        wf.execute("describe-car", input_car)

    return Workflow("quickstart", my_entrypoint)
