from littlehorse.workflow import Workflow, WorkflowThread


def get_workflow() -> Workflow:
    def wfLogic(wf: WorkflowThread) -> None:
        user_id = wf.declare_str("user-id").required()
        message = wf.declare_str("message").required()
        preferred_contact = wf.declare_str("contact-method")
        preferred_contact.assign(wf.execute("fetch-user", user_id))
        wf.do_if(
            preferred_contact.is_equal_to("COMLINK"),
            if_body=lambda ifHandler: ifHandler.execute(
                "send-comlink", user_id, message
            ),
            else_body=lambda elseHandler: elseHandler.execute(
                "send-hologram", user_id, message
            ),
        )

    return Workflow("conditionals-example", wfLogic)
