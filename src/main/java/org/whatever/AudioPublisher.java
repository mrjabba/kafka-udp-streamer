package org.whatever;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Future;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

@Slf4j
public class AudioPublisher {
    public static final String PUBLISH_TOPIC = "TT-AUDIO-2";
    private KafkaProducer producer;

    public AudioPublisher() {
        HashMap<String, Object> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS_CONFIG, "local.docker:9092");
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 8000000);
        this.producer = new KafkaProducer(config, new StringSerializer(), new ByteArraySerializer());
    }

    public void publish(byte[] value) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(PUBLISH_TOPIC, UUID.randomUUID().toString(), value);

        Future result = producer.send(record);

        try {
            Object o = result.get();
            log.info("message sent! o={}", o);

        } catch (Exception e) {
            throw new RuntimeException("ruh roh! ", e);
        }
    }
}
