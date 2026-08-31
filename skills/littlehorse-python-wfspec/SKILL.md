---
name: littlehorse-python-wfspec
description: Build LittleHorse WfSpecs with the Python SDK, including variables, tasks, expressions, control flow, events, threads, child workflows, failures, user tasks, and structs.
---

# LittleHorse Python WfSpecs

Use these fragments when authoring a Python `WfSpec`. Authoring code builds a graph once; it does not run business logic. `WfRunVariable`, `NodeOutput`, and spawned handles are symbolic references.

## Minimal WfSpec

```python
import littlehorse
from littlehorse.config import LHConfig
from littlehorse.workflow import Workflow, WorkflowThread


def entrypoint(wf: WorkflowThread) -> None:
    input_value = wf.declare_str("input").required()
    wf.complete(wf.execute("my-task", input_value))


workflow = Workflow("my-workflow", entrypoint)
littlehorse.create_workflow_spec(workflow, LHConfig())
```

Register referenced `TaskDef`, `StructDef`, `UserTaskDef`, and child `WfSpec` metadata first. Events can be registered with the workflow by the event APIs shown below.

The fragments below assume they are inside an entrypoint or child-thread function and use previously declared values where obvious.

## Variables

```python
s = wf.declare_str("s")
i = wf.declare_int("i")
d = wf.declare_double("d")
b = wf.declare_bool("b")
data = wf.declare_bytes("data")
j = wf.declare_json_obj("j")
json_array = wf.declare_json_arr("json-array")
ts = wf.declare_timestamp("ts")
wf_run_id = wf.declare_wf_run_id("wf-run-id")
person = wf.declare_struct("person", Person)
latest = wf.declare_struct("latest", "person")
v2 = wf.declare_struct("v2", "person", struct_def_version=2)
generic = wf.add_variable("generic", VariableType.STR)
```

Python uses `JSON_ARR` and `JSON_OBJ` variables rather than typed array and map declarations.

```python
wf.declare_str("required").required()
wf.declare_int("defaulted", default_value=3)
wf.declare_json_arr("items", default_value=[1, 2])
wf.declare_json_obj("counts", default_value={"a": 1})
wf.declare_str("searchable").searchable()
wf.declare_json_obj("result").searchable_on("$.score", VariableType.DOUBLE)
wf.declare_str("secret").masked()
wf.declare_str("public").as_public()
wf.declare_str("inherited").as_inherited()
wf.declare_str("private").with_access_level(
    WfRunVariableAccessLevel.PRIVATE_VAR
)
```

```python
s.assign("value")
s.assign(wf.execute("produce-string"))
wf.mutate(s, VariableMutationType.ASSIGN, "value")
wf.execute("consume", s)
wf.execute("consume", "literal")
```

## Outputs And Expressions

```python
out = wf.execute("task", s, 42)
wf.execute("next-task", out)
wf.execute("field-task", j.with_json_path("$.name"))
wf.execute("item-task", json_array.get(0))
wf.execute("field-task", out.with_json_path("$.field"))
wf.execute("struct-field-task", person.get("name"))
```

Use `get()` for struct fields and array indexes. Use `with_json_path()` for JSON variables and JSON task outputs; a path must begin with `$.`.

```python
total = i.multiply(d).add(1).subtract(2).divide(3).pow(2)
total2 = wf.add(i, d)
more = json_array.extend(wf.execute("more-items"))
json_array.assign(more)
```

```python
trimmed = json_array.remove_if_present(1).remove_index(0)
without_key = j.remove_key("secret")
logic = i.is_greater_than(0).do_and(b.is_equal_to(True))
membership = i.is_in(json_array)
contains = json_array.does_contain(i)
```

```python
as_double = s.cast_to(VariableType.DOUBLE)
as_bool = s.cast_to_bool()
as_int = d.cast_to_int()
also_double = i.cast_to_double()
as_string = i.cast_to_str()
as_bytes = s.cast_to_bytes()
as_wf_run_id = s.cast_to_wf_run_id()
```

```python
as_int = wf.execute("produce-double").cast_to_int()
```

```python
message = wf.format("Hello {}; count={}", s, i)
```

## Task Nodes

```python
task = wf.execute("task", s, retries=3, timeout_seconds=60)
wf.complete(task)
```

```python
wf.execute("backoff-task", exponential_backoff=retry_policy)
wf.execute(task_name_variable, s)
wf.execute(wf.format("task-{}", s))
```

Use ordinary Python only to generate repeated graph nodes:

```python
for _ in range(3):
    wf.execute("task")
```

## Conditions And Loops

```python
condition = i.is_greater_than(0)
explicit = wf.condition(b, Comparator.EQUALS, True)
comparisons = i.is_less_than_eq(10).do_and(i.is_greater_than_eq(1))
not_equal = s.is_not_equal_to("x")
```

```python
(
    wf.do_if(condition, lambda yes: yes.execute("yes"))
    .do_else_if(i.is_equal_to(0), lambda zero: zero.execute("zero"))
    .do_else(lambda no: no.execute("no"))
)

wf.do_if(
    b.is_equal_to(True),
    lambda yes: yes.execute("yes"),
    lambda no: no.execute("no"),
)
```

```python
def loop_body(loop: WorkflowThread) -> None:
    i.assign(i.subtract(1))
    loop.execute("tick", i)


wf.do_while(i.is_greater_than(0), loop_body)
```

```python
wf.wait_for_condition(b.is_equal_to(True))
```

`do_while(...)` is a runtime loop; a Python loop only expands the WfSpec during authoring.

## Time And Events

```python
wf.sleep(30)
wf.sleep(i)
wake_at_millis = wf.declare_int("wake-at-ms")
wf.sleep_until(wake_at_millis)
wf.throw_event(
    "order-ready",
    j,
    auto_register=True,
    return_type=dict,
)
```

`sleep_until()` takes an `INT` variable containing epoch milliseconds.

```python
event = wf.wait_for_event(
    "approved",
    timeout=300,
    correlation_id=s,
    mask_correlation_id=True,
    auto_register=True,
    return_type=bool,
    correlated_event_config=correlated_event_config,
)
```

```python
def cancellation_handler(handler: WorkflowThread) -> None:
    reason = handler.declare_str("INPUT")
    handler.execute("cancel", reason)


wf.add_interrupt_handler(
    "cancelled",
    cancellation_handler,
).with_event_type(str)
```

`return_type` supports `str`, `int`, `float`, `bool`, `dict`, and `list` event payloads. Providing `return_type` or `correlated_event_config` also enables registration, so `auto_register=True` is optional in those cases.

## Threads

```python
def child_body(child: WorkflowThread) -> None:
    child.execute("child-task")


child = wf.spawn_thread(child_body, "child", input={})
thread_number = child.number
joined = wf.wait_for_threads(SpawnedThreads.from_list(child))
joined.handle_exception_on_child(
    lambda handler: handler.execute("recover"),
    "declined",
)
joined.handle_error_on_child(
    lambda handler: handler.execute("recover"),
    LHErrorType.TIMEOUT,
)
joined.handle_any_failure_on_child(
    lambda handler: handler.execute("recover")
)
```

```python
def child_with_input(child: WorkflowThread) -> None:
    child_input = child.declare_str("input")
    child.execute("child-task", child_input)


child = wf.spawn_thread(
    child_with_input,
    "child-with-input",
    input={"input": s},
)
```

```python
wf.spawn_thread(child_body, "unjoined-child")
children = SpawnedThreads.from_list(first, second)
wf.wait_for_threads(children)
wf.wait_for_threads(children, WaitForThreadsStrategy.WAIT_FOR_FIRST)
wf.wait_for_threads(children, WaitForThreadsStrategy.WAIT_FOR_ANY)
```

`WAIT_FOR_FIRST` settles on the first completion or failure. `WAIT_FOR_ANY` ignores failures until one succeeds or all fail.

```python
def process_item(child: WorkflowThread) -> None:
    item = child.declare_json_obj("INPUT")
    child.execute("process-item", item)


children = wf.spawn_thread_for_each(
    items,
    process_item,
    "item",
    input={},
)
wf.wait_for_threads(children)
```

`spawn_thread_for_each()` passes each array item to the child as the `INPUT` variable.

## Child Workflows

```python
child = wf.run_wf("child-workflow", inputs={"input": s})
result = wf.wait_for_child_wf(child)
```

Register the child WfSpec before the parent. Python currently requires a literal child WfSpec name and waits from the same workflow thread that started the child.

## Errors And Exceptions

```python
risky = wf.execute("risky")
wf.handle_error(risky, lambda handler: handler.execute("recover-error"))
wf.handle_error(
    risky,
    lambda handler: handler.execute("recover-timeout"),
    LHErrorType.TIMEOUT,
)
wf.handle_exception(
    risky,
    lambda handler: handler.execute("recover-exception"),
)
wf.handle_exception(
    risky,
    lambda handler: handler.execute("recover-decline"),
    "payment-declined",
)
```

```python
wf.handle_any_failure(
    joined,
    lambda handler: handler.execute(
        "compensate",
        handler.declare_json_obj("INPUT"),
    ),
)
wf.fail("order-rejected", "Order was rejected")
wf.fail("order-rejected", "Order was rejected", output=j)
wf.complete()
```

Errors are technical failures; exceptions are named business outcomes. Failure-handler input is conventionally declared with the name `INPUT`.

## User Tasks

```python
approval = wf.assign_user_task(
    "approval-form",
    user_id=user_id,
    user_group="approvers",
)
group_task = wf.assign_user_task(
    "approval-form",
    user_group="approvers",
)
```

```python
approval.with_notes(wf.format("Approve order {}", order_id))
approval.with_on_cancellation_exception("approval-cancelled")
wf.release_to_group_on_deadline(approval, 60)
wf.schedule_reminder_task(approval, 60, "send-reminder", email)
wf.cancel_user_task_run_after(approval, 3600)
wf.cancel_user_task_run_after_assignment(approval, 3600)
wf.reassign_user_task_on_deadline(
    approval,
    60,
    user_id="next-user",
)
b.assign(approval.with_json_path("$.approved"))
```

Configure a `UserTaskOutput` and its deadline actions immediately after assignment; these methods reject stale user-task handles.

Register the form schema first:

```python
from littlehorse.model import PutUserTaskDefRequest, UserTaskField, VariableType

request = PutUserTaskDefRequest(
    name="approval-form",
    fields=[
        UserTaskField(
            name="approved",
            display_name="Approved",
            required=True,
            type=VariableType.BOOL,
        )
    ],
)
config.stub().PutUserTaskDef(request)
```

## Structs

```python
from typing import Annotated

from littlehorse.lh_struct import LHStructField, lh_struct_def


@lh_struct_def(name="address", description="A postal address.")
class Address:
    city: str


@lh_struct_def(name="person", description="A person.")
class Person:
    name: Annotated[
        str,
        LHStructField(description="Display name", masked=False),
    ]
    address: Address
```

```python
littlehorse.create_struct_def(Address, config)
littlehorse.create_struct_def(Person, config)

person = wf.declare_struct("person", Person)
city = person.get("address").get("city")
wf.execute("process-person", person, city)
```

Register nested StructDefs before the StructDefs that reference them. Python does not provide a workflow-time struct builder for composing a Struct from symbolic values.

## Hierarchical WfSpecs

Declare a child WfSpec and inherited variable:

```python
def child_entrypoint(wf: WorkflowThread) -> None:
    shared = wf.declare_str("shared").as_inherited()


child = Workflow(
    "child",
    child_entrypoint,
    parent_wf="parent",
)


def parent_entrypoint(wf: WorkflowThread) -> None:
    shared = wf.declare_str("shared").as_public()


parent = Workflow("parent", parent_entrypoint)
```

## Registration Variants

```python
littlehorse.create_workflow_spec(workflow, config)
littlehorse.create_workflow_spec(workflow, config, timeout=30)
```

`LHConfig()` reads LittleHorse connection settings from `LHC_*` environment variables.

## Workflow Configuration And Inspection

```python
workflow = Workflow("wf", entrypoint, parent_wf="parent")
workflow.with_task_timeout_seconds(60)
workflow.with_retry_policy(
    retries=3,
    exponential_backoff=retry_policy,
)
workflow.with_update_type(AllowedUpdateType.MINOR_REVISION_UPDATES)
workflow.with_retention_policy(workflow_retention_policy)
```

Set a thread retention policy inside its initializer:

```python
wf.with_retention_policy(thread_retention_policy)
```

```python
# Each of these compiles the Workflow; use the form needed by your application.
request = workflow.compile()
json = str(Workflow("wf", entrypoint))
Workflow("wf", entrypoint).save("build/my-workflow.json")
workflow.name
workflow.external_events_to_register
workflow.workflow_events_to_register
```
