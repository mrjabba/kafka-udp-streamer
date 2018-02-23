package org.whatever;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.whatever.Constants.KAFKA_BOOTSTRAP_SERVERS;
import static org.whatever.Constants.LOCAL_AUDIO_STORE_PATH;

@Slf4j
public class AudioMessageHandler {
    private String topic;
    private KafkaConsumer consumer;

    public AudioMessageHandler(String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BOOTSTRAP_SERVERS);
        // For now, just use a random group ID until we get further along
        props.put("group.id", UUID.randomUUID().toString());
        props.put("key.deserializer", StringDeserializer.class);
        props.put("value.deserializer", ByteArrayDeserializer.class);
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", false);
        props.put("max.partition.fetch.bytes", 3000000);
        this.consumer = new KafkaConsumer<>(props);
    }

    public List<String> consume() {
        log.info("Consuming!");
        ArrayList<String> songNames = new ArrayList<>();
        try {
            System.out.println("<<<< subscribing...");
            consumer.subscribe(Arrays.asList(topic));
            System.out.println("<<<< subscribed...");

            int attempts = 0;
            while(attempts < 10) {
                attempts++;
                ConsumerRecords<String, byte[]> records = consumer.poll(1000);
                for (ConsumerRecord<String, byte[]> record : records) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("partition", record.partition());
                    data.put("offset", record.offset());
                    data.put("value", record.value());
                    log.info("<< Consumed audio record={}", data);

                    // FIXME should determine how to detect content type (hard code for mp3 right now)
                    File audioFile = new File(LOCAL_AUDIO_STORE_PATH + "/" + record.key());
                    songNames.add(record.key());
                    try {
                        FileUtils.writeByteArrayToFile(audioFile, record.value());;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
        }
        return songNames;
    }

    public void shutdown() {
        consumer.wakeup();
    }
}
