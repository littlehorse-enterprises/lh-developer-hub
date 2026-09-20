import asyncio

import littlehorse
from littlehorse.config import LHConfig
from littlehorse.worker import LHTaskWorker, WorkerContext

VERIFY_IDENTITY_TASK = "verify-identity"
NOTIFY_VERIFIED_TASK = "notify-customer-verified"
NOTIFY_NOT_VERIFIED_TASK = "notify-customer-not-verified"


async def verify_identity(
    full_name: str, email: str, ssn: int, context: WorkerContext
) -> str:
    del ssn, context
    return f"Verification request accepted for {full_name} at {email}"


async def notify_customer_verified(
    full_name: str, email: str, context: WorkerContext
) -> str:
    del context
    return f"Notified {full_name} at {email} that their identity was verified"


async def notify_customer_not_verified(
    full_name: str, email: str, context: WorkerContext
) -> str:
    del context
    return f"Notified {full_name} at {email} that their identity was not verified"


async def main() -> None:
    config = LHConfig()
    await littlehorse.start(
        LHTaskWorker(verify_identity, VERIFY_IDENTITY_TASK, config),
        LHTaskWorker(notify_customer_verified, NOTIFY_VERIFIED_TASK, config),
        LHTaskWorker(notify_customer_not_verified, NOTIFY_NOT_VERIFIED_TASK, config),
    )


if __name__ == "__main__":
    asyncio.run(main())
