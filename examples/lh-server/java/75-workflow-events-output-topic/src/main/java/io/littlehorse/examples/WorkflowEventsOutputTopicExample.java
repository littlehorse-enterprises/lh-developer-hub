package io.littlehorse.examples;

import io.littlehorse.sdk.common.LHLibUtil;
import io.littlehorse.sdk.common.config.LHConfig;
import io.littlehorse.sdk.common.proto.LittleHorseGrpc.LittleHorseBlockingStub;
import io.littlehorse.sdk.common.proto.OutputTopicConfig;
import io.littlehorse.sdk.common.proto.OutputTopicRecord;
import io.littlehorse.sdk.common.proto.PutTenantRequest;
import io.littlehorse.sdk.common.proto.WorkflowEvent;
import io.littlehorse.sdk.common.proto.WorkflowRetentionPolicy;
import io.littlehorse.sdk.wfsdk.Workflow;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

public final class WorkflowEventsOutputTopicExample {

    private static final String WF_NAME = "workflow-events-output-topic";
    private static final String EVENT_NAME = "shipment-updated";

    private WorkflowEventsOutputTopicExample() {}

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static Workflow workflow() {
        Workflow workflow = Workflow.newWorkflow(WF_NAME, wf -> {
            var shipmentId = wf.declareStr("shipment-id").required();
            wf.throwEvent(EVENT_NAME, wf.format("Shipment {0} is ready", shipmentId))
                    .registeredAs(String.class);
            wf.complete();
        });
        return workflow.withRetentionPolicy(WorkflowRetentionPolicy.newBuilder()
                .setSecondsAfterWfTermination(Duration.ofDays(14).toSeconds())
                .build());
    }

    private static Properties kafkaProperties(String groupId) {
        Properties properties = new Properties();
        properties.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                env("LH_KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                OutputTopicRecordDeserializer.class.getName());
        return properties;
    }

    private static void printWorkflowEvent(ConsumerRecord<String, OutputTopicRecord> record) {
        OutputTopicRecord output = record.value();
        if (output == null
                || output.getPayloadCase() != OutputTopicRecord.PayloadCase.WORKFLOW_EVENT) {
            return;
        }

        WorkflowEvent event = output.getWorkflowEvent();
        String eventDefinition = event.getId().getWorkflowEventDefId().getName();
        String wfRunId = event.getId().getWfRunId().getId();
        System.out.printf(
                "WorkflowEvent definition=%s wfRunId=%s timestamp=%s content=%s%n",
                eventDefinition,
                wfRunId,
                event.getCreatedAt(),
                LHLibUtil.protoToJson(event.getContent()));
    }

    public static void main(String[] args) {
        LHConfig config = new LHConfig();
        LittleHorseBlockingStub client = config.getBlockingStub();

        String tenantId = env("LH_TENANT_ID", "default");
        String clusterName = env("LH_CLUSTER_NAME", "cluster1");
        String outputTopic = env(
                "LH_OUTPUT_TOPIC", clusterName + "_" + tenantId + "_execution");
        String groupId = env("LH_KAFKA_GROUP_ID", "workflow-events-output-topic-example");

        // An empty config enables the execution and metadata output topics for this tenant.
        client.putTenant(PutTenantRequest.newBuilder()
                .setId(tenantId)
                .setOutputTopicConfig(OutputTopicConfig.newBuilder().build())
                .build());
        workflow().registerWfSpec(config);

        System.out.printf(
                "Listening for WORKFLOW_EVENT records on %s via %s (tenant %s).%n",
                outputTopic,
                env("LH_KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
                tenantId);

        try (KafkaConsumer<String, OutputTopicRecord> consumer =
                new KafkaConsumer<>(kafkaProperties(groupId))) {
            consumer.subscribe(List.of(outputTopic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            try {
                while (true) {
                    for (ConsumerRecord<String, OutputTopicRecord> record :
                            consumer.poll(Duration.ofSeconds(1))) {
                        printWorkflowEvent(record);
                    }
                }
            } catch (WakeupException shutdown) {
                // The shutdown hook wakes poll(); try-with-resources then closes the consumer.
            }
        }
    }
}
