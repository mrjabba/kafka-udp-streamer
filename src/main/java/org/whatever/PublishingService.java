package org.whatever;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class PublishingService {
    private AudioPublisher audioPublisher;

    public PublishingService(AudioPublisher audioPublisher) {
        this.audioPublisher = audioPublisher;
    }

    public void publish(File file) {
        log.info("publishing song={}", file.getAbsolutePath());
        Path path = Paths.get(file.toURI());
        try {
            byte[] bytes = Files.readAllBytes(path);
            audioPublisher.publish(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
