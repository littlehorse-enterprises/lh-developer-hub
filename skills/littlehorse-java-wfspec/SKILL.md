---
name: littlehorse-java-wfspec
description: Build LittleHorse WfSpecs with the Java SDK, including variables, tasks, expressions, control flow, events, threads, child workflows, failures, user tasks, and structs.
---

# LittleHorse Java WfSpecs

Use these fragments when authoring a Java `WfSpec`. Authoring code builds a graph once; it does not run business logic. `WfRunVariable`, `NodeOutput`, and spawned handles are symbolic references.

## Minimal WfSpec

```java
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.Workflow;

Workflow workflow = Workflow.newWorkflow("my-workflow", wf -> {
    WfRunVariable input = wf.declareStr("input").required();
    wf.complete(wf.execute("my-task", input));
});

workflow.registerWfSpec(new LHConfig().getBlockingStub());
```

Register referenced `TaskDef`, `StructDef`, `UserTaskDef`, child `WfSpec`, and event metadata first unless an API such as `registeredAs(...)` does it.

The fragments below assume they are inside `wf -> { ... }` and use previously declared values where obvious.

## Variables

```java
WfRunVariable s = wf.declareStr("s");
WfRunVariable i = wf.declareInt("i");
WfRunVariable d = wf.declareDouble("d");
WfRunVariable b = wf.declareBool("b");
WfRunVariable bytes = wf.declareBytes("bytes");
WfRunVariable j = wf.declareJsonObj("j");
WfRunVariable jsonArray = wf.declareJsonArr("json-array");
WfRunVariable ts = wf.declareTimestamp("ts");
WfRunVariable xs = wf.declareArray("xs", Long.class);
WfRunVariable map = wf.declareMap("map", String.class, Long.class);
WfRunVariable person = wf.declareStruct("person", Person.class);
WfRunVariable latest = wf.declareStruct("latest", "person");
WfRunVariable v2 = wf.declareStruct("v2", "person", 2);
WfRunVariable generic = wf.addVariable("generic", VariableType.STR);
```

```java
wf.declareStr("required").required();
wf.declareInt("defaulted").withDefault(3);
wf.declareTimestamp("when").withDefault(Instant.parse("2026-01-01T00:00:00Z"));
wf.declareArray("xs", Long.class).withDefault(new Long[] {1L, 2L});
wf.declareMap("counts", String.class, Long.class).withDefault(Map.of("a", 1L));
wf.declareStr("searchable").searchable();
wf.declareJsonObj("result").searchableOn("$.score", VariableType.DOUBLE);
wf.declareStr("secret").masked();
wf.declareStr("private").withAccessLevel(WfRunVariableAccessLevel.PRIVATE_VAR);
```

```java
s.assign("value");
s.assign(wf.execute("produce-string"));
wf.mutate(s, VariableMutationType.ASSIGN, "value");
wf.execute("consume", s);
wf.execute("consume", "literal");
```

## Outputs And Expressions

```java
NodeOutput out = wf.execute("task", s, 42);
wf.execute("next-task", out);
wf.execute("field-task", j.jsonPath("$.name"));
wf.execute("field-task", map.get("key"));
wf.execute("item-task", jsonArray.get(0));
wf.execute("field-task", out.get("field"));
```

```java
LHExpression total = i.multiply(d).add(1).subtract(2).divide(3).pow(2);
LHExpression total2 = wf.add(i, d);
LHExpression more = xs.extend(wf.execute("more-items"));
xs.assign(more);
```

```java
LHExpression trimmed = xs.removeIfPresent(1).removeIndex(0);
LHExpression withoutKey = j.removeKey("secret");
LHExpression count = xs.size();
LHExpression logic = b.and(true).or(false);
LHExpression membership = i.isIn(xs);
LHExpression contains = xs.doesContain(i);
```

```java
LHExpression asDouble = s.castTo(VariableType.DOUBLE);
LHExpression asBool = s.castToBool();
LHExpression asInt = out.castToInt();
LHExpression alsoDouble = out.castToDouble();
LHExpression asString = out.castToStr();
LHExpression asBytes = out.castToBytes();
LHExpression asWfRunId = out.castToWfRunId();
```

```java
LHFormatString message = wf.format("Hello {0}; count={1}", s, i);
```

## Task Nodes

```java
TaskNodeOutput task = wf.execute("task", s).withRetries(3).timeout(60);
wf.complete(task);
```

```java
wf.execute("backoff-task").withExponentialBackoff(retryPolicy);
wf.execute(taskNameVariable, s);
wf.execute(wf.format("task-{0}", s));
```

Use ordinary Java only to generate repeated graph nodes:

```java
for (int n = 0; n < 3; n++) wf.execute("task");
```

## Conditions And Loops

```java
LHExpression condition = i.isGreaterThan(0);
LHExpression explicit = wf.condition(b, Comparator.EQUALS, true);
LHExpression comparisons = i.isLessThanEq(10).and(i.isGreaterThanEq(1));
LHExpression notEqual = s.isNotEqualTo("x");
```

```java
wf.doIf(condition, yes -> yes.execute("yes"))
  .doElseIf(i.isEqualTo(0), zero -> zero.execute("zero"))
  .doElse(no -> no.execute("no"));
wf.doIfElse(b, yes -> yes.execute("yes"), no -> no.execute("no"));
```

```java
wf.doWhile(i.isGreaterThan(0), loop -> {
    i.assign(i.subtract(1));
    loop.execute("tick", i);
});
```

```java
wf.waitForCondition(b.isEqualTo(true));
```

`doWhile(...)` is a runtime loop; a Java loop only expands the WfSpec during authoring.

## Time And Events

```java
wf.sleepSeconds(30);
wf.sleepSeconds(i);
WfRunVariable epochMillis = wf.declareInt("wake-at-ms");
wf.sleepUntil(epochMillis);
wf.throwEvent("order-ready", j).registeredAs(Map.class);
```

```java
ExternalEventNodeOutput event = wf.waitForEvent("approved")
    .timeout(300).withCorrelationId(s, true)
    .withCorrelatedEventConfig(correlatedEventConfig).registeredAs(Boolean.class);
```

```java
wf.registerInterruptHandler("cancelled", handler -> {
    WfRunVariable reason = handler.declareStr(WorkflowThread.HANDLER_INPUT_VAR);
    handler.execute("cancel", reason);
}).withEventType(String.class);
```

## Threads

```java
ThreadFunc childBody = child -> child.execute("child-task");
SpawnedThread child = wf.spawnThread(childBody, "child", Map.of());
WfRunVariable threadNumber = child.getThreadNumberVariable();
WaitForThreadsNodeOutput joined = wf.waitForThreads(SpawnedThreads.of(child));
joined.handleExceptionOnChild("declined", handler -> handler.execute("recover"));
joined.handleErrorOnChild(LHErrorType.TIMEOUT, handler -> handler.execute("recover"));
joined.handleAnyFailureOnChild(handler -> handler.execute("recover"));
```

```java
SpawnedThread child = wf.spawnThread(childWf -> {
    WfRunVariable childInput = childWf.declareStr("input");
    childWf.execute("child-task", childInput);
}, "child", Map.of("input", s));
```

```java
wf.spawnThread(childBody, "unjoined-child", null);
wf.waitForThreads(SpawnedThreads.of(first, second));
wf.waitForFirstOf(SpawnedThreads.of(first, second));
wf.waitForAnyOf(SpawnedThreads.of(first, second));
```

`waitForFirstOf(...)` settles on the first completion or failure. `waitForAnyOf(...)` ignores failures until one succeeds or all fail.

```java
SpawnedThreads children = wf.spawnThreadForEach(j.jsonPath("$.items"), "item", child -> {
    WfRunVariable item = child.declareJsonObj(WorkflowThread.HANDLER_INPUT_VAR);
    child.execute("process-item", item);
}, Map.of());
wf.waitForThreads(children);
```

## Child Workflows

```java
SpawnedChildWf child = wf.runWf("child-workflow", Map.of("input", s));
SpawnedChildWf dynamic = wf.runWf(wfSpecNameVariable, Map.of());
NodeOutput result = wf.waitForChildWf(child);
```

Register the child WfSpec before the parent.

## Errors And Exceptions

```java
NodeOutput risky = wf.execute("risky");
wf.handleError(risky, handler -> handler.execute("recover-error"));
wf.handleError(risky, LHErrorType.TIMEOUT, handler -> handler.execute("recover-timeout"));
wf.handleException(risky, handler -> handler.execute("recover-exception"));
wf.handleException(risky, "payment-declined", handler -> handler.execute("recover-decline"));
```

```java
wf.handleAnyFailure(joined, handler -> {
    WfRunVariable failure = handler.declareJsonObj(WorkflowThread.HANDLER_INPUT_VAR);
    handler.execute("compensate", failure);
});
wf.fail("order-rejected", "Order was rejected");
wf.fail(j, "order-rejected", "Order was rejected");
wf.complete();
```

Errors are technical failures; exceptions are named business outcomes.

## User Tasks

```java
UserTaskOutput approval = wf.assignUserTask("approval-form", userId, "approvers");
UserTaskOutput groupTask = wf.assignUserTask("approval-form", null, "approvers");
```

```java
approval.withNotes(wf.format("Approve order {0}", orderId));
approval.withOnCancellationException("approval-cancelled");
wf.releaseToGroupOnDeadline(approval, 60);
wf.scheduleReminderTask(approval, 60, "send-reminder", email);
wf.scheduleReminderTaskOnAssignment(approval, 60, "send-reminder", email);
wf.cancelUserTaskRunAfter(approval, 3600);
wf.cancelUserTaskRunAfterAssignment(approval, 3600);
wf.reassignUserTask(approval, "next-user", null, 60);
b.assign(approval.jsonPath("$.approved"));
```

Register the form schema first:

```java
class ApprovalForm {
    @UserTaskField public boolean approved;
}
client.putUserTaskDef(new UserTaskSchema(new ApprovalForm(), "approval-form").compile());
```

## Structs

```java
@LHStructDef("person")
@Getter @Setter
public class Person {
    @LHStructField(description = "Display name", masked = false, isNullable = true)
    String name;
}
```

```java
LHStructBuilder value = wf.buildStruct("person")
    .put("name", s)
    .put("address", wf.buildInlineStruct().put("city", "Boston"));
LHStructBuilder versioned = wf.buildStruct("person", 2);
person.assign(value);
```

Register nested StructDefs before the StructDefs that reference them.

## Hierarchical WfSpecs

Declare a child WfSpec and inherited variable:

```java
Workflow child = Workflow.newWorkflow("child", wf -> {
    WfRunVariable shared = wf.declareStr("shared").asInherited();
});
child.setParent("parent");

Workflow parent = Workflow.newWorkflow("parent", wf -> {
    wf.declareStr("shared").asPublic();
});
```

Use the three-argument `newWorkflow(...)` overload to resolve placeholders in referenced StructDef names:

```java
Workflow templated = Workflow.newWorkflow("wf", wf -> { /* ... */ }, Map.of("company", "acme"));
```

## Registration Variants

```java
workflow.registerWfSpec(config.getBlockingStub());
workflow.registerWfSpec(config); // preserves configured Java type adapters
```

## Workflow Configuration And Inspection

```java
workflow.setParent("parent");
workflow.setDefaultTaskTimeout(60);
workflow.setDefaultTaskRetries(3);
workflow.setDefaultTaskExponentialBackoffPolicy(retryPolicy);
workflow.withUpdateType(AllowedUpdateType.MINOR_REVISION_ONLY);
workflow.withRetentionPolicy(workflowRetentionPolicy);
workflow.withDefaultThreadRetentionPolicy(threadRetentionPolicy);
wf.withRetentionPolicy(threadRetentionPolicy);
```

```java
PutWfSpecRequest request = workflow.compileWorkflow();
PutWfSpecRequest adapted = workflow.compileWorkflow(config);
String json = workflow.compileWfToJson();
workflow.compileAndSaveToDisk("build/wfspecs");
workflow.doesWfSpecExist(config.getBlockingStub());
workflow.doesWfSpecExist(config.getBlockingStub(), 2);
workflow.getName();
workflow.getRequiredTaskDefNames();
workflow.getRequiredExternalEventDefNames();
workflow.getRequiredWorkflowEventDefNames();
workflow.getRequiredChildWfSpecNames();
workflow.getExternalEventDefsToRegister();
workflow.getWorkflowEventDefsToRegister();
```
