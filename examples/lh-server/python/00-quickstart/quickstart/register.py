import littlehorse
from littlehorse.config import LHConfig
from littlehorse.model import CorrelatedEventConfig, LHErrorType
from littlehorse.workflow import Workflow, WorkflowThread

from quickstart.workers import (
    NOTIFY_NOT_VERIFIED_TASK,
    NOTIFY_VERIFIED_TASK,
    VERIFY_IDENTITY_TASK,
    notify_customer_not_verified,
    notify_customer_verified,
    verify_identity,
)

WF_SPEC_NAME = "quickstart"
IDENTITY_VERIFIED_EVENT = "identity-verified"


def quickstart_workflow(wf: WorkflowThread) -> None:
    full_name = wf.declare_str("full-name").searchable().required()
    email = wf.declare_str("email").searchable().required()
    ssn = wf.declare_int("ssn").masked().required()
    identity_verified = wf.declare_bool("identity-verified").searchable()

    wf.execute(VERIFY_IDENTITY_TASK, full_name, email, ssn, retries=3)

    verification = wf.wait_for_event(
        IDENTITY_VERIFIED_EVENT,
        timeout=300,
        correlation_id=email,
        return_type=bool,
        correlated_event_config=CorrelatedEventConfig(
            delete_after_first_correlation=False
        ),
    )

    def handle_timeout(handler: WorkflowThread) -> None:
        handler.execute(NOTIFY_NOT_VERIFIED_TASK, full_name, email)
        handler.fail(
            "customer-not-verified", "Unable to verify customer identity in time."
        )

    wf.handle_error(verification, handle_timeout, LHErrorType.TIMEOUT)
    identity_verified.assign(verification)
    wf.do_if(
        identity_verified.is_equal_to(True),
        lambda yes: yes.execute(NOTIFY_VERIFIED_TASK, full_name, email),
        lambda no: no.execute(NOTIFY_NOT_VERIFIED_TASK, full_name, email),
    )


def main() -> None:
    config = LHConfig()
    littlehorse.create_task_def(verify_identity, VERIFY_IDENTITY_TASK, config)
    littlehorse.create_task_def(
        notify_customer_verified, NOTIFY_VERIFIED_TASK, config
    )
    littlehorse.create_task_def(
        notify_customer_not_verified, NOTIFY_NOT_VERIFIED_TASK, config
    )
    littlehorse.create_workflow_spec(
        Workflow(WF_SPEC_NAME, quickstart_workflow), config
    )
    print(f"Registered TaskDefs, ExternalEventDef, and WfSpec {WF_SPEC_NAME}")


if __name__ == "__main__":
    main()
