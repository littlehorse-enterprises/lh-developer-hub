from littlehorse.workflow import Workflow, WorkflowThread


def get_workflow() -> Workflow:
    def wfLogic(wf: WorkflowThread) -> None:
        price = wf.declare_double("price").required()
        item_id = wf.declare_str("item")
        item_id.assign("lightsaber")
        user_id = wf.declare_str("user-id")
        user_id.assign("obiwan")
        charge_credit_card_handle = wf.execute("charge-credit-card", user_id, price)

        def insufficient_funds(handler: WorkflowThread) -> None:
            handler.execute("cancel-order-insufficient-funds", user_id)
            handler.fail(
                "insufficient-funds",
                "Credit card did not have sufficient funds",
            )

        def technical_failure(handler: WorkflowThread) -> None:
            handler.execute("notify-order-failed", user_id)
            handler.fail("technical-failure", "Failed to charge credit card")

        wf.handle_exception(
            charge_credit_card_handle,
            insufficient_funds,
            "insufficient-funds",
        )
        wf.handle_error(charge_credit_card_handle, technical_failure)
        wf.execute("ship-item", item_id, user_id)

    return Workflow("exception-example", wfLogic)
