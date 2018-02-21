package org.whatever;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

@Configuration
public class AudioConfiguration {
    private String topic = "TT-AUDIO";

    @Bean
    public AudioPublisher audioPublisher(KafkaProducer producer) {
        return new AudioPublisher(topic, producer);
    }

    @Bean
    public KafkaProducer producer() {
        HashMap<String, Object> config = new HashMap<>();
        config.put(BOOTSTRAP_SERVERS_CONFIG, "local.docker:9092");
        // shouldn't KEY_SERIALIZER_CLASS_CONFIG and VALUE_SERIALIZER_CLASS_CONFIG b/c specified in constructor?
        return new KafkaProducer(config, new StringSerializer(), new StringSerializer());
    }
}
