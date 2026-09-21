from littlehorse.workflow import Workflow, WorkflowThread


def get_workflow() -> Workflow:
    def wfLogic(wf: WorkflowThread) -> None:
        user_id = wf.declare_str("user-id").searchable().required()
        favorite_team = wf.declare_str("favorite-team")
        favorite_number = wf.declare_int("favorite-player-number")
        user_task_def_name = "report-favorite-player"
        form_result = wf.assign_user_task(user_task_def_name, user_id)
        favorite_team.assign(form_result.with_json_path("$.favorite-team"))
        favorite_number.assign(form_result.with_json_path("$.favorite-player-number"))
        wf.execute("report-favorite-player", user_id, favorite_team, favorite_number)

    return Workflow("favorite-player-demo", wfLogic)
