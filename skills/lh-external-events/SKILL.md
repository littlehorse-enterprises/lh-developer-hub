---
name: lh-external-event
description: This skill defines external events in littlehorse. How a littlehorse workflow can pause and wait for an external system to send an event.

* An `ExternalEventDef` is a metadata object for an `ExternalEvent`
* An `ExternalEvent` is an instance of an `ExternalEventDef`
* An `ExternalEvent` must be associated with a `WfRunId` by definition
* If the system posting the events doesn't know the `WfRunId` in advance, use a `CorrelatedEvent`
* A `WfSpec` can have a node that waits for an `ExternalEvent` 

To register an externalEventDef, you can