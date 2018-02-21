package org.whatever;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Future;

@Slf4j
@Component
public class AudioPublisher {
    private String topic;
    private KafkaProducer producer;

    public AudioPublisher(String topic, KafkaProducer producer) {
        this.topic = topic;
        this.producer = producer;

        // publish a message or two
        publish();
        publish();
    }

    public void publish() {
        ProducerRecord<String, Object> record = new ProducerRecord<>(topic, UUID.randomUUID().toString(), "some stuff-" + System.currentTimeMillis());

        Future result = producer.send(record);

        try {
            Object o = result.get();
            log.info("message sent! o={}", o);

        } catch (Exception e) {
            throw new RuntimeException("ruh roh! ", e);
        }
    }
}
