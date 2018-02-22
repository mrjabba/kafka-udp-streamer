package org.whatever;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class AudioHacking extends Application {
    private static final Logger log = LoggerFactory.getLogger(AudioHacking.class);

    public static void main(String[] args) {
        SpringApplication.run(AudioHacking.class, args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Button startButton = new Button();
        Button stopButton = new Button();
        Text actionMessage = new Text();

        startButton.setText("Start");
        stopButton.setText("Stop");

        primaryStage.setTitle("hello something something");

        File sampleFile = new File("samples/demo.mp3");

        Media media = new Media(sampleFile.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        MediaView mediaView = new MediaView(mediaPlayer);

        VBox vBoxRoot = new VBox(10, mediaView, startButton, stopButton, actionMessage);
        vBoxRoot.setAlignment(Pos.CENTER);

        System.out.println("setting scene...");
        Scene mediaScene = new Scene(vBoxRoot, 300, 500);

        primaryStage.setScene(mediaScene);
        primaryStage.show();

        startButton.setOnAction(event -> {
            mediaPlayer.play();
            actionMessage.setText("Playing...");
        });

        stopButton.setOnAction(event -> {
            actionMessage.setText("Stopping...");
            mediaPlayer.stop();
            actionMessage.setText("Stopped");
        });
    }
}
