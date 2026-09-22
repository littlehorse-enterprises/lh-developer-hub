from littlehorse.workflow import Workflow, WorkflowThread


def get_workflow() -> Workflow:
    def wfLogic(wf: WorkflowThread) -> None:
        user_id = wf.declare_str("user-id").required().searchable()
        user = wf.declare_json_obj("user")
        age = wf.declare_int("age")
        fetched_user = wf.execute("fetch-user", user_id)
        user.assign(fetched_user)
        age.assign(fetched_user.with_json_path("$.age"))
        message = wf.format(
            "Hello there, {0}! You are {1} years old",
            user.with_json_path("$.title"),
            age,
        )
        wf.execute("send-email", user.with_json_path("$.email"), message)

    return Workflow("variables-example", wfLogic)
