package org.whatever;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class AudioMessageHandler {
    private String topic;
    private KafkaConsumer consumer;

    public AudioMessageHandler(String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put("bootstrap.servers", "local.docker:9092");
        // For now, just use a random group ID until we get further along
        props.put("group.id", UUID.randomUUID().toString());
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", ByteArrayDeserializer.class);
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", false);
        this.consumer = new KafkaConsumer<>(props);
    }

    public void consume() {
        try {
            System.out.println("<<<< subscribing...");
            consumer.subscribe(Arrays.asList(topic));
            System.out.println("<<<< subscribed...");

            while (true) {
                ConsumerRecords<String, byte[]> records = consumer.poll(Long.MAX_VALUE);
                for (ConsumerRecord<String, byte[]> record : records) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());
                    System.out.println(">>>>> " + data);
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
        }
    }

    public void shutdown() {
        consumer.wakeup();
    }
}
