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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class AudioHacking extends Application {

    private PublishingService publishingService;

    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.publishingService = new PublishingService(new AudioPublisher());

        Button startButton = new Button();
        Button stopButton = new Button();
        Button chooseDirectoryButton = new Button();
        Text actionMessage = new Text();

        startButton.setText("Start");
        stopButton.setText("Stop");
        chooseDirectoryButton.setText("Choose Directory");


        primaryStage.setTitle("hello something something");

        File sampleFile = new File("samples/demo.mp3");

        Media media = new Media(sampleFile.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        MediaView mediaView = new MediaView(mediaPlayer);

        VBox vBoxRoot = new VBox(10, mediaView, startButton, stopButton, actionMessage, chooseDirectoryButton);
        vBoxRoot.setAlignment(Pos.CENTER);

        System.out.println("setting scene...");
        Scene mediaScene = new Scene(vBoxRoot, 300, 500);

        primaryStage.setScene(mediaScene);
        primaryStage.show();

        chooseDirectoryButton.setOnAction(event -> {
            pickDirectory(primaryStage);
        });

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

    private void pickDirectory(Stage primaryStage) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("imma chooser");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(primaryStage);
        for (File file : selectedDirectory.listFiles()) {
            publishingService.publish(file);

        }
        log.info("> selectedDirectory={}", selectedDirectory);
    }
}
