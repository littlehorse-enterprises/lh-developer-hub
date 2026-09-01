import static io.apicurio.registry.serde.config.SerdeConfig.DESERIALIZER_SPECIFIC_VALUE_RETURN_CLASS;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import io.apicurio.registry.serde.jsonschema.JsonSchemaKafkaDeserializer;
import io.apicurio.registry.serde.jsonschema.JsonSchemaKafkaSerializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public final class App {

    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(1);

    private App() {}

    public static void main(String[] args) {
        if (args.length != 2 || (!args[0].equals("producer") && !args[0].equals("consumer"))) {
            System.err.println("Usage: App <producer|consumer> <properties-file>");
            System.exit(1);
        }

        Properties properties = loadProperties(Path.of(args[1]));
        String topic = requireProperty(properties, "apicurio.registry.artifact.artifact-id");
        if (args[0].equals("producer")) {
            runProducer(properties, topic);
        } else {
            runConsumer(properties, topic);
        }
    }

    private static Properties loadProperties(Path path) {
        Properties properties = new Properties();
        try (var input = Files.newInputStream(path)) {
            properties.load(input);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + path.toAbsolutePath(), e);
        }
        return properties;
    }

    private static String requireProperty(Properties properties, String name) {
        String value = properties.getProperty(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required property: " + name);
        }
        return value;
    }

    private static void runProducer(Properties properties, String topic) {
        properties.remove("key.deserializer");
        properties.remove("value.deserializer");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSchemaKafkaSerializer.class.getName());
        properties.putIfAbsent("acks", "all");

        try (KafkaProducer<String, StreamletEvent> producer = new KafkaProducer<>(properties);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            System.out.println("Producing to " + topic + ". Enter a message, or an empty line to stop.");
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                ProducerRecord<String, StreamletEvent> record =
                        new ProducerRecord<>(topic, UUID.randomUUID().toString(), new StreamletEvent(line));
                RecordMetadata metadata = producer.send(record).get();
                System.out.printf(
                        "Produced to partition %d at offset %d%n", metadata.partition(), metadata.offset());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to produce Streamlet records", e);
        }
    }

    private static void runConsumer(Properties properties, String topic) {
        properties.remove("key.serializer");
        properties.remove("value.serializer");
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonSchemaKafkaDeserializer.class.getName());
        properties.put(DESERIALIZER_SPECIFIC_VALUE_RETURN_CLASS, StreamletEvent.class.getName());
        properties.putIfAbsent("auto.offset.reset", "earliest");

        try (KafkaConsumer<String, StreamletEvent> consumer = new KafkaConsumer<>(properties)) {
            consumer.subscribe(List.of(topic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            System.out.println("Consuming from " + topic + ". Press Ctrl+C to stop.");

            while (true) {
                for (ConsumerRecord<String, StreamletEvent> record : consumer.poll(POLL_TIMEOUT)) {
                    System.out.printf(
                            "Consumed message '%s' from partition %d at offset %d (key=%s)%n",
                            record.value().getMessage(), record.partition(), record.offset(), record.key());
                }
            }
        } catch (WakeupException ignored) {
            System.out.println("Consumer stopped.");
        }
    }
}
