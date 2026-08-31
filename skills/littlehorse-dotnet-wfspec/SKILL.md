---
name: littlehorse-dotnet-wfspec
description: Build LittleHorse WfSpecs with the .NET SDK, including variables, tasks, expressions, control flow, events, threads, child workflows, failures, user tasks, and structs.
---

# LittleHorse .NET WfSpecs

Use these fragments when authoring a C# `WfSpec`. Authoring code builds a graph once; it does not run business logic. `WfRunVariable`, `NodeOutput`, and spawned handles are symbolic references.

## Minimal WfSpec

```csharp
using LittleHorse.Sdk;
using LittleHorse.Sdk.Workflow.Spec;

void Entrypoint(WorkflowThread wf)
{
    WfRunVariable input = wf.DeclareStr("input").Required();
    wf.Complete(wf.Execute("my-task", input));
}

var workflow = new Workflow("my-workflow", Entrypoint);
await workflow.RegisterWfSpec(client);
```

Register referenced `TaskDef`, `StructDef`, `UserTaskDef`, and child `WfSpec` metadata first. Event APIs can mark their definitions for registration with the workflow.

The fragments below assume they are inside an entrypoint or child-thread function and use previously declared values where obvious.

## Variables

```csharp
WfRunVariable s = wf.DeclareStr("s");
WfRunVariable i = wf.DeclareInt("i");
WfRunVariable d = wf.DeclareDouble("d");
WfRunVariable b = wf.DeclareBool("b");
WfRunVariable bytes = wf.DeclareBytes("bytes");
WfRunVariable j = wf.DeclareJsonObj("j");
WfRunVariable jsonArray = wf.DeclareJsonArr("json-array");
WfRunVariable ts = wf.DeclareTimestamp("ts");
WfRunVariable wfRunId = wf.DeclareWfRunId("wf-run-id");
WfRunVariable names = wf.DeclareArray("names", typeof(string));
WfRunVariable person = wf.DeclareStruct("person", typeof(Person));
WfRunVariable latest = wf.DeclareStruct("latest", "person");
WfRunVariable v2 = wf.DeclareStruct("v2", "person", 2);
WfRunVariable generic = wf.AddVariable("generic", VariableType.Str);
```

```csharp
wf.DeclareStr("required").Required();
wf.DeclareInt("defaulted").WithDefault(3);
wf.DeclareStr("searchable").Searchable();
wf.DeclareJsonObj("result").SearchableOn("$.score", VariableType.Double);
wf.DeclareStr("secret").Masked();
wf.DeclareStr("public").AsPublic();
wf.DeclareStr("inherited").AsInherited();
wf.DeclareStr("private").WithAccessLevel(WfRunVariableAccessLevel.PrivateVar);
```

`WithDefault()` supports primitive variables. The .NET SDK does not support struct defaults.

```csharp
s.Assign("value");
s.Assign(wf.Execute("produce-string"));
wf.Mutate(i, VariableMutationType.Assign, 1);
wf.Execute("consume", s);
wf.Execute("consume", "literal");
```

## Outputs And Expressions

```csharp
TaskNodeOutput output = wf.Execute("task", s, 42);
wf.Execute("next-task", output);
wf.Execute("field-task", j.WithJsonPath("$.name"));
wf.Execute("field-task", output.WithJsonPath("$.field"));
```

`WithJsonPath()` may be applied once to a JSON variable or node-output handle.

```csharp
LHExpression total = i.Multiply(d).Add(1).Subtract(2).Divide(3).Pow(2);
LHExpression more = jsonArray.Extend(wf.Execute("more-items"));
jsonArray.Assign(more);

LHExpression trimmed = jsonArray.RemoveIfPresent(1).RemoveIndex(0);
LHExpression withoutKey = j.RemoveKey("secret");
LHExpression logic = i.IsGreaterThan(0).And(b.IsEqualTo(true));
LHExpression membership = i.IsIn(new[] { 1, 2, 3 });
LHExpression contains = jsonArray.DoesContain(i);
```

```csharp
LHExpression asDouble = s.CastTo(VariableType.Double);
LHExpression asBool = s.CastToBool();
LHExpression asInt = output.CastToInt();
LHExpression alsoDouble = output.CastToDouble();
LHExpression asString = output.CastToStr();
LHExpression asBytes = output.CastToBytes();
LHExpression asWfRunId = output.CastToWfRunId();
```

```csharp
LHFormatString message = wf.Format("Hello {0}; count={1}", s, i);
```

`Format()` accepts `WfRunVariable` arguments, not node outputs or expressions. Assign those to variables first.

## Task Nodes

```csharp
TaskNodeOutput task = wf.Execute("task", s)
    .WithRetries(3)
    .WithTimeout(60);
wf.Complete(task);
```

```csharp
wf.Execute("backoff-task").WithExponentialBackoff(retryPolicy);
wf.Execute(taskNameVariable, s);
wf.Execute(wf.Format("task-{0}", taskNameVariable), s);
```

Use ordinary C# only to generate repeated graph nodes:

```csharp
for (int n = 0; n < 3; n++) wf.Execute("task");
```

## Conditions And Loops

```csharp
LHExpression condition = i.IsGreaterThan(0);
LHExpression explicitCondition = wf.Condition(b, Comparator.Equals, true);
LHExpression comparisons = i.IsLessThanEq(10).And(i.IsGreaterThanEq(1));
LHExpression notEqual = s.IsNotEqualTo("x");
```

```csharp
wf.DoIf(condition, yes => yes.Execute("yes"))
    .DoElseIf(i.IsEqualTo(0), zero => zero.Execute("zero"))
    .DoElse(no => no.Execute("no"));

wf.DoIf(b, yes => yes.Execute("yes"), no => no.Execute("no"));
```

```csharp
wf.DoWhile(i.IsGreaterThan(0), loop =>
{
    i.Assign(i.Subtract(1));
    loop.Execute("tick", i);
});

wf.WaitForCondition(b);
```

`DoWhile()` is a runtime loop; a C# loop only expands the WfSpec during authoring.

## Time And Events

```csharp
wf.SleepSeconds(30);
wf.SleepSeconds(i);
WfRunVariable epochMillis = wf.DeclareInt("wake-at-ms");
wf.SleepUntil(epochMillis);
wf.ThrowEvent("order-ready", j).RegisteredAs(typeof(Dictionary<string, object>));
```

```csharp
ExternalEventNodeOutput approval = wf.WaitForEvent("approved")
    .WithTimeout(300)
    .WithCorrelationId(s, masked: true)
    .WithCorrelatedEventConfig(correlatedEventConfig)
    .RegisteredAs(typeof(bool));
```

```csharp
wf.RegisterInterruptHandler("cancelled", handler =>
{
    WfRunVariable reason = handler.DeclareStr(WorkflowThread.HandlerInputVar);
    handler.Execute("cancel", reason);
}).WithEventType(typeof(string));
```

`WorkflowThread.HandlerInputVar` is the reserved variable name `INPUT`.

## Threads

```csharp
SpawnedThread child = wf.SpawnThread(
    "child",
    childThread => childThread.Execute("child-task"),
    new Dictionary<string, object>());

WaitForThreadsNodeOutput joined = wf.WaitForThreads(SpawnedThreads.Of(child));
joined.HandleExceptionOnChild(handler => handler.Execute("recover"), "declined");
joined.HandleErrorOnChild(handler => handler.Execute("recover"), LHErrorType.Timeout);
joined.HandleAnyFailureOnChild(handler => handler.Execute("recover"));
```

```csharp
SpawnedThread child = wf.SpawnThread(
    "child-with-input",
    childThread =>
    {
        WfRunVariable childInput = childThread.DeclareStr("input");
        childThread.Execute("child-task", childInput);
    },
    new Dictionary<string, object> { ["input"] = s });
```

```csharp
wf.WaitForThreads(SpawnedThreads.Of(first, second));
wf.WaitForFirstOf(SpawnedThreads.Of(first, second));
wf.WaitForAnyOf(SpawnedThreads.Of(first, second));
```

`WaitForFirstOf()` settles on the first completion or failure. `WaitForAnyOf()` ignores failures until one succeeds or all fail.

```csharp
SpawnedThreads children = wf.SpawnThreadForEach(
    jsonArray,
    "item",
    childThread =>
    {
        WfRunVariable item = childThread.DeclareJsonObj(WorkflowThread.HandlerInputVar);
        childThread.Execute("process-item", item);
    });
wf.WaitForThreads(children);
```

`SpawnThreadForEach()` iterates a `JSON_ARR` variable and passes each item as `INPUT`.

## Child Workflows

```csharp
SpawnedChildWf child = wf.RunWf(
    "child-workflow",
    new Dictionary<string, object> { ["input"] = s });
NodeOutput result = wf.WaitForChildWf(child);
```

Register the child WfSpec before the parent. The .NET SDK currently requires a literal child WfSpec name.

## Errors And Exceptions

```csharp
TaskNodeOutput risky = wf.Execute("risky");
wf.HandleError(risky, handler => handler.Execute("recover-error"));
wf.HandleError(risky, LHErrorType.Timeout, handler => handler.Execute("recover-timeout"));
wf.HandleException(risky, handler => handler.Execute("recover-exception"));
wf.HandleException(risky, "payment-declined", handler => handler.Execute("recover-decline"));
wf.HandleAnyFailure(risky, handler => handler.Execute("recover"));
```

```csharp
wf.HandleAnyFailure(joined, handler =>
{
    WfRunVariable failure = handler.DeclareJsonObj(WorkflowThread.HandlerInputVar);
    handler.Execute("compensate", failure);
});
wf.Fail("order-rejected", "Order was rejected");
wf.Fail(j, "order-rejected", "Order was rejected");
wf.Complete();
```

Errors are technical failures; exceptions are named business outcomes.

## User Tasks

```csharp
UserTaskOutput approval = wf.AssignUserTask(
    "approval-form",
    userId: userId,
    userGroup: "approvers");
UserTaskOutput groupTask = wf.AssignUserTask(
    "approval-form",
    userId: null,
    userGroup: "approvers");
```

```csharp
approval.WithNotes(wf.Format("Approve order {0}", orderId));
approval.WithOnCancellationException("approval-cancelled");
wf.ReleaseToGroupOnDeadline(approval, 60);
wf.ScheduleReminderTask(approval, 60, "send-reminder", email);
wf.ScheduleReminderTaskOnAssignment(approval, 60, "send-reminder", email);
wf.CancelUserTaskRunAfter(approval, 3600);
wf.CancelUserTaskRunAfterAssignment(approval, 3600);
wf.ReassignUserTask(approval, "next-user", null, 60);
b.Assign(approval.WithJsonPath("$.approved"));
```

Configure deadline actions immediately after assignment; these methods reject stale user-task handles.

Register the form schema first:

```csharp
public class ApprovalForm
{
    [UserTaskField(DisplayName = "Approved?")]
    public bool Approved;
}

var schema = new UserTaskSchema(new ApprovalForm(), "approval-form");
await client.PutUserTaskDefAsync(schema.Compile());
```

## Structs

```csharp
[LHStructDef("person")]
public class Person
{
    [LHStructField(masked: false)]
    public string Name { get; set; } = "";

    [LHStructIgnore]
    public string LocalOnly { get; set; } = "";
}
```

```csharp
var type = new LHStructDefType(typeof(Person));
var request = new PutStructDefRequest
{
    Name = type.GetStructDefId().Name,
    Description = type.GetStructDefDescription(),
    StructDef = type.GetInlineStructDef(),
    AllowedUpdates = StructDefCompatibilityType.NoSchemaUpdates
};
await client.PutStructDefAsync(request);

WfRunVariable person = wf.DeclareStruct("person", typeof(Person));
```

Register nested StructDefs before the StructDefs that reference them. The .NET SDK does not provide typed workflow-time struct-field navigation or a symbolic struct builder.

## Hierarchical WfSpecs

`AsPublic()` and `AsInherited()` variable metadata are available, but the current .NET `Workflow` API has no public parent-WfSpec setter. It cannot author a complete hierarchical WfSpec relationship.

## Registration Variants

```csharp
await workflow.RegisterWfSpec(client);

PutWfSpecRequest request = workflow.Compile();
await client.PutWfSpecAsync(request);
```

## Workflow Configuration And Inspection

```csharp
workflow.SetDefaultTaskTimeout(60);
workflow.SetDefaultTaskRetries(3);
workflow.SetDefaultTaskExponentialBackoffPolicy(retryPolicy);
workflow.WithUpdateType(AllowedUpdateType.MinorRevisionOnly);
workflow.WithRetentionPolicy(workflowRetentionPolicy);
workflow.WithDefaultThreadRetentionPolicy(threadRetentionPolicy);
wf.WithRetentionPolicy(threadRetentionPolicy);
```

```csharp
PutWfSpecRequest request = workflow.Compile();
string? json = workflow.CompileWfToJson();
workflow.CompileAndSaveToDisk("build/wfspecs");
bool exists = workflow.DoesWfSpecExist(client);
bool v2Exists = workflow.DoesWfSpecExist(client, 2);
string name = workflow.GetName();
HashSet<string> tasks = workflow.GetRequiredTaskDefNames();
HashSet<string> externalEvents = workflow.GetRequiredExternalEventDefNames();
HashSet<string> workflowEvents = workflow.GetRequiredWorkflowEventDefNames();
```

Call `Compile()` before dependency inspection for predictable results.
