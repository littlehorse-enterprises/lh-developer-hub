package io.littlehorse.examples;

import com.google.protobuf.InvalidProtocolBufferException;
import io.littlehorse.sdk.common.proto.OutputTopicRecord;
import org.apache.kafka.common.serialization.Deserializer;

public final class OutputTopicRecordDeserializer implements Deserializer<OutputTopicRecord> {

    @Override
    public OutputTopicRecord deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return OutputTopicRecord.parseFrom(data);
        } catch (InvalidProtocolBufferException exception) {
            throw new IllegalArgumentException("Invalid OutputTopicRecord on topic " + topic, exception);
        }
    }
}
