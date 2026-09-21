from littlehorse.workflow import Workflow, WorkflowThread


class UnderpantsWorkflow:
    def __init__(self) -> None:
        self.all_underpants = None

    def wfLogic(self, wf: WorkflowThread) -> None:
        self.all_underpants = wf.declare_json_arr("all-underpants")
        wf.execute("start-underpants-collection")
        wf.wait_for_event("done-collecting-underpants")
        wf.execute("profit", self.all_underpants)
        wf.add_interrupt_handler("underpant-collected", self.handle_underpant)

    def handle_underpant(self, wf: WorkflowThread) -> None:
        interrupt_content = wf.declare_str("INPUT")
        self.all_underpants.assign(self.all_underpants.add(interrupt_content))
        wf.execute("collect-underpant", interrupt_content)

    def get_workflow(self) -> Workflow:
        return Workflow("collect-underpants", self.wfLogic)
