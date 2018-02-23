package org.whatever;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.concurrent.Future;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.whatever.Constants.AUDIO_TOPIC;
import static org.whatever.Constants.KAFKA_BOOTSTRAP_SERVERS;

@Slf4j
public class AudioPublisher {
    private KafkaProducer producer;

    public AudioPublisher() {
        HashMap<String, Object> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 8000000);
        this.producer = new KafkaProducer(config, new StringSerializer(), new ByteArraySerializer());
    }

    public void publish(String name, byte[] value) {
        ProducerRecord<String, Object> record = new ProducerRecord<>(AUDIO_TOPIC, name, value);

        Future result = producer.send(record);

        try {
            Object o = result.get();
            log.info("message sent! o={}", o);

        } catch (Exception e) {
            throw new RuntimeException("ruh roh! ", e);
        }
    }
}
