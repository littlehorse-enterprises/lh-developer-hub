from littlehorse.model import CorrelatedEventConfig
from littlehorse.workflow import Workflow, WorkflowThread

EVENT_NAME = "document-signed"


def get_workflow() -> Workflow:
    def wf_logic(wf: WorkflowThread) -> None:
        document_id = wf.declare_str("document-id")
        signer_name = wf.wait_for_event(
            EVENT_NAME,
            correlation_id=document_id,
            correlated_event_config=CorrelatedEventConfig(
                delete_after_first_correlation=True
            ),
            return_type=str,
        )
        wf.execute("processSignedDocument", document_id, signer_name)

    return Workflow("correlated-event-example", wf_logic)
