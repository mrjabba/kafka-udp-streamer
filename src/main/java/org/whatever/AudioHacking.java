package org.whatever;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.whatever.domain.Playlist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.whatever.Constants.AUDIO_TOPIC;
import static org.whatever.Constants.LOCAL_AUDIO_STORE_PATH;
import static org.whatever.Constants.LOGO_IMAGE;

@Slf4j
public class AudioHacking extends Application {

    private PublishingService publishingService;
    private AudioMessageHandler audioMessageHandler;
    private int currentTrackId = 0;

    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        log.info("===== starting app! ====");

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                                                         fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClient mongoClient = new MongoClient("localhost",
                                                  MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());

        MongoDatabase db = mongoClient.getDatabase("playlists");

        MongoCollection<Playlist> collection = db.getCollection("playlistCollection", Playlist.class);

//        List<Song> songs = new ArrayList<>();
//        songs.add(Song.builder().artist("me")
//                .title("awesome song")
//                .offset(1)
//                .build());
//        songs.add(Song.builder().artist("me")
//                .title("stupendous song")
//                .offset(2)
//                .build());
//        Playlist samplePlaylist = Playlist.builder().name("sample playlist 1")
//                .topic("samplePlaylist1")
//                .songs(songs)
//                .build();
//
//        collection.replaceOne(eq("name", samplePlaylist.getName()), samplePlaylist, new UpdateOptions().upsert(true).bypassDocumentValidation(true));
//
//        FindIterable<Playlist> results = collection.find();
//
//        Block<Playlist> printBlock = System.out::println;
//
//        results.forEach(printBlock);

        this.publishingService = new PublishingService(new AudioPublisher());
        this.audioMessageHandler = new AudioMessageHandler(AUDIO_TOPIC);

        Image logoImage = new Image(LOGO_IMAGE, 300, 400, false, true);
        BackgroundImage backgroundImage= new BackgroundImage(logoImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                                                             BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

        Button startButton = new Button();
        Button stopButton = new Button();
        Button nextTrackButton = new Button();
        Button previousTrackButton = new Button();
        Button chooseDirectoryButton = new Button();

        Text titleLogoText = new Text("KafkaAmp!");
        Text actionMessage = new Text();

        startButton.setText("Play");
        stopButton.setText("Stop");
        nextTrackButton.setText(">>");
        previousTrackButton.setText("<<");
        chooseDirectoryButton.setText("Choose Directory");


        primaryStage.setTitle("KafkaAmp");

        playIntroSound();


        File audioDirectory = new File(LOCAL_AUDIO_STORE_PATH);
        File[] mp3Files = audioDirectory.listFiles((d, s) -> {
            return s.toLowerCase().endsWith("mp3");
        });

        List<MediaView> mediaViews = new ArrayList<>();

        for (File mp3File : mp3Files) {
            Media media = new Media(mp3File.toURI().toString());
            mediaViews.add(new MediaView(new MediaPlayer(media)));
        }

        HBox logoBox = new HBox();
        logoBox.getChildren().addAll(titleLogoText);

        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(previousTrackButton, startButton, nextTrackButton, stopButton, chooseDirectoryButton);

        VBox vBoxRoot = new VBox(10);
        vBoxRoot.setAlignment(Pos.CENTER);
        vBoxRoot.getChildren().addAll(logoBox, buttonBox, actionMessage);

        vBoxRoot.setBackground(new Background(backgroundImage));

        System.out.println("setting scene...");
        Scene mediaScene = new Scene(vBoxRoot, 500, 500);

        primaryStage.setScene(mediaScene);
        primaryStage.show();

        chooseDirectoryButton.setOnAction(event -> {
            pickDirectory(primaryStage);
        });

        startButton.setOnAction(event -> {
            mediaViews.get(currentTrackId).getMediaPlayer().play();
            actionMessage.setText("Playing...");
        });

        stopButton.setOnAction(event -> {
            actionMessage.setText("Stopping...");
            mediaViews.get(currentTrackId).getMediaPlayer().stop();
            actionMessage.setText("Stopped");
        });

        nextTrackButton.setOnAction(event -> nextTrack(mediaViews));
        previousTrackButton.setOnAction(event -> previousTrack(mediaViews));
    }

    private void playIntroSound() {
        File sampleFile = new File("samples/demo.mp3");
        Media sampleMedia = new Media(sampleFile.toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sampleMedia);
        mediaPlayer.play();
    }

    private void nextTrack(List<MediaView> mediaViews) {
        mediaViews.get(currentTrackId).getMediaPlayer().stop();
        if (currentTrackId == mediaViews.size()-1) {
            currentTrackId = 0;
        }
        else {
            currentTrackId++;
        }
        mediaViews.get(currentTrackId).getMediaPlayer().play();
    }

    private void previousTrack(List<MediaView> mediaViews) {
        mediaViews.get(currentTrackId).getMediaPlayer().stop();
        if (currentTrackId == 0) {
            currentTrackId = 0;
        }
        else {
            currentTrackId--;
        }
        mediaViews.get(currentTrackId).getMediaPlayer().play();
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
