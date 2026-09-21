from littlehorse.workflow import Workflow, WorkflowThread

EXTERNAL_EVENT_NAME = "name-posted"


def get_workflow() -> Workflow:
    def wfLogic(wf: WorkflowThread) -> None:
        name = wf.declare_str("name")
        event_payload = wf.wait_for_event(EXTERNAL_EVENT_NAME)
        name.assign(event_payload)
        wf.execute("greet", name)

    return Workflow("external-event-example", wfLogic)
