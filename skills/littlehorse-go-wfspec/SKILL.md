---
name: littlehorse-go-wfspec
description: Build LittleHorse WfSpecs with the Go SDK, including variables, tasks, expressions, control flow, events, threads, child workflows, failures, user tasks, and structs.
---

# LittleHorse Go WfSpecs

Use these fragments when authoring a Go `WfSpec`. Authoring code builds a graph once; it does not run business logic. `WfRunVariable`, `NodeOutput`, and spawned handles are symbolic references.

## Minimal WfSpec

```go
import (
    "context"

    "github.com/littlehorse-enterprises/littlehorse/sdk-go/lhproto"
    "github.com/littlehorse-enterprises/littlehorse/sdk-go/littlehorse"
)

func entrypoint(wf *littlehorse.WorkflowThread) {
    input := wf.DeclareStr("input").Required()
    wf.Complete(wf.Execute("my-task", input))
}

workflow := littlehorse.NewWorkflow(entrypoint, "my-workflow")
request, err := workflow.Compile()
if err != nil {
    panic(err)
}
_, err = (*client).PutWfSpec(context.Background(), request)
```

`workflow.RegisterWfSpec(*client)` is a convenience that also registers event metadata, but it does not return registration errors. Register referenced `TaskDef`, `StructDef`, `UserTaskDef`, and child `WfSpec` metadata first.

The fragments below assume they are inside an entrypoint or child-thread function and use previously declared values where obvious.

## Variables

```go
b := wf.DeclareBool("b")
i := wf.DeclareInt("i")
s := wf.DeclareStr("s")
d := wf.DeclareDouble("d")
data := wf.DeclareBytes("data")
j := wf.DeclareJsonObj("j")
items := wf.DeclareJsonArr("items")
ts := wf.DeclareTimestamp("ts")
wfRunID := wf.DeclareWfRunId("wf-run-id")
person := wf.DeclareStruct("person", "person")
v2 := wf.DeclareStructWithVersion("v2", "person", 2)
generic := wf.AddVariable("generic", lhproto.VariableType_STR)
```

Go uses `JSON_ARR` and `JSON_OBJ` variables rather than typed array and map declarations.

```go
wf.DeclareStr("required").Required()
wf.DeclareInt("defaulted").WithDefault(3)
wf.DeclareJsonArr("items").WithDefault([]int{1, 2})
wf.DeclareJsonObj("counts").WithDefault(map[string]int{"a": 1})
wf.DeclareStr("searchable").Searchable()
wf.DeclareJsonObj("result").SearchableOn("$.score", lhproto.VariableType_DOUBLE)
wf.DeclareStr("secret").MaskedValue()
wf.DeclareStr("public").AsPublic()
wf.DeclareStr("inherited").AsInherited()
```

`AsPublic()`, `AsInherited()`, and `WithAccessLevel()` mutate the variable definition but return a value rather than a pointer; do not continue a pointer-returning fluent chain after them.

```go
s.Assign("value")
s.Assign(wf.Execute("produce-string"))
wf.Mutate(i, lhproto.VariableMutationType_ASSIGN, 1)
wf.Execute("consume", s)
wf.Execute("consume", "literal")
```

## Outputs And Expressions

```go
out := wf.Execute("task", s, 42)
wf.Execute("next-task", out)
wf.Execute("field-task", j.Get("name"))
wf.Execute("item-task", items.GetIndex(0))
wf.Execute("field-task", out.Get("field"))
wf.Execute("nested-task", out.Get("address").Get("city"))
```

Prefer `Get()` and `GetIndex()` path navigation. `JsonPath("$.field")` remains available but cannot be mixed with native path navigation on the same handle.

```go
total := i.Multiply(d).Add(1).Subtract(2).Divide(3).Pow(2)
total2 := wf.Add(i, d)
more := items.Extend(wf.Execute("more-items"))
items.Assign(more)

trimmed := items.RemoveIfPresent(1).RemoveIndex_ByInt(0)
withoutKey := j.RemoveKey("secret")
logic := i.IsGreaterThan(0).And(b.IsEqualTo(true))
membership := i.IsIn([]int{1, 2, 3})
contains := items.DoesContain(i)
```

```go
asDouble := s.CastTo(lhproto.VariableType_DOUBLE)
asBool := s.CastToBool()
asInt := out.CastToInt()
alsoDouble := out.CastToDouble()
asString := out.CastToStr()
asBytes := out.CastToBytes()
asWfRunID := out.CastToWfRunId()
```

```go
message := wf.Format("Hello, {0}; count={1}", s, i)
```

`Format()` accepts `*WfRunVariable` arguments, not node outputs or expressions. Assign those to variables first.

## Task Nodes

```go
task := wf.Execute("task", s).WithRetries(3).Timeout(60)
wf.Complete(task)
```

```go
wf.Execute("backoff-task").WithExponentialBackoff(retryPolicy)
wf.Execute(taskNameVariable, s)
wf.Execute(wf.Format("task-{0}", taskNameVariable), s)
```

Use ordinary Go only to generate repeated graph nodes:

```go
for n := 0; n < 3; n++ {
    wf.Execute("task")
}
```

## Conditions And Loops

```go
condition := i.IsGreaterThan(0)
comparisons := i.IsLessThanEq(10).And(i.IsGreaterThanEq(1))
notEqual := s.IsNotEqualTo("x")
```

```go
wf.DoIf(condition, func(yes *littlehorse.WorkflowThread) {
    yes.Execute("yes")
}).DoElseIf(i.IsEqualTo(0), func(zero *littlehorse.WorkflowThread) {
    zero.Execute("zero")
}).DoElse(func(no *littlehorse.WorkflowThread) {
    no.Execute("no")
})
```

```go
wf.DoWhile(i.IsGreaterThan(0), func(loop *littlehorse.WorkflowThread) {
    i.Assign(i.Subtract(1))
    loop.Execute("tick", i)
})

wf.WaitForCondition(b.IsEqualTo(true))
```

`DoWhile()` is a runtime loop; a Go loop only expands the WfSpec during authoring.

## Time And Events

```go
wf.Sleep(30)
wf.ThrowEvent("order-ready", j).RegisteredAs(lhproto.VariableType_JSON_OBJ)
```

Go's `Sleep()` accepts a compile-time `int` number of seconds. It does not support a workflow variable or absolute timestamp.

```go
event := wf.WaitForEvent("approved").
    RegisteredAs(lhproto.VariableType_BOOL).
    Timeout(300).
    SetCorrelationId(s).
    MaskCorrelationId(true).
    WithCorrelatedEventConfig(correlatedEventConfig)
```

Use `RegisteredAsEmpty()` for an event with no payload.

```go
wf.HandleInterrupt("cancelled", func(handler *littlehorse.WorkflowThread) {
    reason := handler.DeclareStr("INPUT")
    handler.Execute("cancel", reason)
}).RegisteredAs(lhproto.VariableType_STR)
```

## Threads

```go
child := wf.SpawnThread(
    func(childThread *littlehorse.WorkflowThread) {
        childThread.Execute("child-task")
    },
    "child",
    map[string]interface{}{},
)

joined := wf.WaitForThreads(child)
declined := "declined"
joined.HandleExceptionOnChild(
    func(handler *littlehorse.WorkflowThread) { handler.Execute("recover") },
    &declined,
)
timeout := string(littlehorse.Timeout)
joined.HandleErrorOnChild(
    func(handler *littlehorse.WorkflowThread) { handler.Execute("recover") },
    &timeout,
)
joined.HandleAnyFailureOnChild(
    func(handler *littlehorse.WorkflowThread) { handler.Execute("recover") },
)
```

```go
child := wf.SpawnThread(
    func(childThread *littlehorse.WorkflowThread) {
        input := childThread.DeclareStr("input")
        childThread.Execute("child-task", input)
    },
    "child-with-input",
    map[string]interface{}{"input": s},
)
```

```go
wf.WaitForThreads(first, second)
wf.WaitForFirstOf(first, second)
wf.WaitForAnyOf(first, second)
```

`WaitForFirstOf()` settles on the first completion or failure. `WaitForAnyOf()` ignores failures until one succeeds or all fail.

```go
inputs := map[string]interface{}{}
children := wf.SpawnThreadForEach(
    items,
    "item",
    func(childThread *littlehorse.WorkflowThread) {
        item := childThread.DeclareJsonObj("INPUT")
        childThread.Execute("process-item", item)
    },
    &inputs,
)
wf.WaitForThreadsList(children)
```

List variants also exist for first/any waits: `WaitForFirstOfList()` and `WaitForAnyOfList()`.

## Child Workflows

```go
child := wf.RunWf(
    "child-workflow",
    map[string]interface{}{"input": s},
)
result := wf.WaitForChildWf(child)
```

```go
dynamic := wf.RunWf(wfSpecNameVariable, map[string]interface{}{})
wf.WaitForChildWf(dynamic)
```

Register the child WfSpec before the parent.

## Errors And Exceptions

```go
risky := wf.Execute("risky")

timeout := littlehorse.Timeout
wf.HandleError(risky, &timeout, func(handler *littlehorse.WorkflowThread) {
    handler.Execute("recover-timeout")
})
wf.HandleError(risky, nil, func(handler *littlehorse.WorkflowThread) {
    handler.Execute("recover-error")
})

declined := "payment-declined"
wf.HandleException(risky, &declined, func(handler *littlehorse.WorkflowThread) {
    handler.Execute("recover-decline")
})
wf.HandleException(risky, nil, func(handler *littlehorse.WorkflowThread) {
    handler.Execute("recover-exception")
})
```

```go
wf.HandleAnyFailure(joined, func(handler *littlehorse.WorkflowThread) {
    failure := handler.DeclareJsonObj("INPUT")
    handler.Execute("compensate", failure)
})

message := "Order was rejected"
wf.Fail(j, "order-rejected", &message)
```

Errors are technical failures; exceptions are named business outcomes. A task implementation returns `*littlehorse.LHTaskException` to produce a named exception.

## User Tasks

```go
approval := wf.AssignUserTask(
    "approval-form",
    userID,
    "approvers",
).WithNotes("Please review").
    WithOnCancellationException("approval-cancelled")

groupTask := wf.AssignUserTask("approval-form", nil, "approvers")
```

```go
wf.ReleaseToGroupOnDeadline(approval, 60)
wf.ScheduleReminderTask(approval, 60, "send-reminder", email)
wf.ScheduleReminderTaskOnAssignment(approval, 60, "send-reminder", email)
wf.CancelUserTaskAfter(approval, 3600)
wf.CancelUserTaskAfterAssignment(approval, 3600)
wf.ReassignUserTaskOnDeadline(approval, "next-user", nil, 60)
b.Assign(approval.Get("approved"))
```

Register forms using `lhproto.PutUserTaskDefRequest` and `PutUserTaskDef`; the Go SDK does not provide a form-schema builder.

## Structs

```go
type Person struct {
    Name   string `lh:"name"`
    Secret string `lh:"secret,masked"`
    Local  string `lh:"-"`
}

func (Person) LHStructDef() littlehorse.LHStructDefInfo {
    return littlehorse.LHStructDefInfo{
        Name:        "person",
        Description: "A person",
    }
}
```

```go
compatibility := lhproto.StructDefCompatibilityType_NO_SCHEMA_UPDATES
err := littlehorse.RegisterStructDef(*client, Person{}, &compatibility)
if err != nil {
    panic(err)
}

person := wf.DeclareStruct("person", "person")
name := person.Get("name")
wf.Execute("process-person", person, name)
```

Register nested StructDefs before the StructDefs that reference them. Reflected StructDefs do not support non-byte slices, arrays, or maps.

## Hierarchical WfSpecs

Go has `AsPublic()` and `AsInherited()` variable metadata, but no high-level API for declaring a parent WfSpec. Parent-child run hierarchy is created with `RunWf()`.

## Registration Variants

```go
workflow.RegisterWfSpec(*client)
```

Use explicit compilation and `PutWfSpec` when errors must be handled:

```go
request, err := workflow.Compile()
if err != nil {
    panic(err)
}
_, err = (*client).PutWfSpec(context.Background(), request)
```

## Workflow Configuration And Inspection

```go
workflow := littlehorse.NewWorkflow(entrypoint, "wf").
    WithUpdateType(lhproto.AllowedUpdateType_MINOR_REVISION).
    WithRetentionPolicy(workflowRetentionPolicy)
```

Set thread retention inside its initializer:

```go
wf.WithRetentionPolicy(threadRetentionPolicy)
```

```go
request, err := workflow.Compile()
if err != nil {
    panic(err)
}
littlehorse.PrintProto(request)
```

The Go SDK does not expose dependency-name inventories or graph inspection before compilation.
