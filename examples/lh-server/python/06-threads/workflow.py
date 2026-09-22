from littlehorse.workflow import SpawnedThreads, Workflow, WorkflowThread


class MyWorkflow:
    def __init__(self) -> None:
        self.parent_variable = None

    def entrypoint_thread_logic(self, wf: WorkflowThread) -> None:
        self.parent_variable = wf.declare_str("parent-var")
        self.parent_variable.assign("This is the parent variable's initial value")
        wf.execute("my-task", self.parent_variable)
        child = wf.spawn_thread(
            self.child_thread_logic,
            thread_name="child",
            input={"child-input": "This is the input to the child thread"},
        )
        wf.sleep(25)
        wf.execute("my-task", self.parent_variable)
        wf.wait_for_threads(SpawnedThreads.from_list(child))

    def child_thread_logic(self, wf: WorkflowThread) -> None:
        child_input = wf.declare_str("child-input").required()
        wf.execute("my-task", child_input)
        wf.execute("my-task", self.parent_variable)
        self.parent_variable.assign(
            "This is the value of the parent variable set by the child."
        )
        wf.sleep(45)

    def get_workflow(self) -> Workflow:
        return Workflow("threads-example", self.entrypoint_thread_logic)
