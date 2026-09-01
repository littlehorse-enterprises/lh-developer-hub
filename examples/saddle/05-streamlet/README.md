# Saddle Streamlet Quickstart

A Saddle Streamlet is a Kafka topic paired with a JSON Schema in the Apicurio Registry. This quickstart creates one in the Saddle UI, configures Kafka and schema-registry access, and runs a Java application as either a producer or consumer.

## Step 1: Create a Streamlet

In Saddle, expand **Stream** in the left sidebar and select **Streamlets**. Click **Add Streamlet** and configure:

- **Name:** `quickstart-events`
- **Description:** `Events for the Streamlet quickstart`
- **Partitions:** `1`
- **Cleanup Policy:** **Delete**
- **Retention:** keep the defaults

Replace the value schema with the following JSON Schema:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "StreamletEvent",
  "type": "object",
  "properties": {
    "message": {
      "type": "string",
      "description": "Message carried by the event."
    }
  },
  "required": ["message"],
  "additionalProperties": true
}
```

Click **Create Streamlet** and wait until its status is **READY**. Saddle prefixes the physical Kafka topic with your tenant ID, so its **Topic Name** will not be exactly `quickstart-events`.

## Step 2: Configure Kafka Access

Click **Connect** on the Streamlet detail page to open the StreamSense Connect Wizard.

1. Create a **StreamSense Client**. This supplies the SASL/SCRAM credentials used to connect to Kafka.
2. Create a **Schemas Client**. This supplies OAuth credentials used by the Apicurio serializers and deserializers.
3. Confirm that `quickstart-events` is selected and set the consumer group to `<TENANT_ID>.quickstart`, replacing `<TENANT_ID>` with your Saddle tenant ID.
4. Download **Kafka Client and Apicurio Ser/Des Configuration**.

The credentials are displayed only when each client is created and are held by the wizard only while it remains open. Keep the downloaded file private and do not commit it.

Move the downloaded file into this example directory as `streamlet.properties`:

```bash
mv ~/Downloads/streamsense-quickstart-events.properties \
  examples/saddle/05-streamlet/streamlet.properties
```

The generated properties combine two independent connections: StreamSense credentials for Kafka and Schemas credentials for the schema registry. The application supplies the producer- or consumer-specific Ser/Des settings at runtime.

## Step 3: Consume Records

From the repository root, start the application in consumer mode:

```bash
./gradlew :examples:saddle:java:05-streamlet:run \
  --args="consumer streamlet.properties"
```

The consumer reads records with the Apicurio JSON Schema deserializer and prints each message. Leave it running.

## Step 4: Produce Records

In another terminal, run the same application in producer mode:

```bash
./gradlew :examples:saddle:java:05-streamlet:run \
  --args="producer streamlet.properties"
```

The application reads the physical topic from the downloaded Apicurio configuration. Type a message and press Enter. The producer wraps it in a `StreamletEvent`, validates and serializes it using the Streamlet's registered schema, and publishes it to Kafka. The consumer terminal will print the message, partition, offset, and record key.

Enter an empty line to stop the producer. Press Ctrl+C to stop the consumer.
