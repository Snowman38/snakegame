import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import handlers.AudioController;
import handlers.FileHandler;
import interfaces.GameConstants;
import interfaces.PathConstants;
import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import logging.Logger;
import server.game.managers.mapmanager.MapSkinManager;
import server.game.managers.snakemanager.SnakeSkinManager;

public class MainClient extends Application implements GameConstants, PathConstants {

    /**
     * The main function called at the start of the program
     * @param args Java program arguments
     */
    public static void main(String[] args) {

        Logger.enableLogging();
        launch(args);
    }

    /**
     * The start function called internally by JavaFX
     * @param primaryStage the root stage provided by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {

        try {

            File tracker = new File(TRACKER_PATH);
            if (tracker.createNewFile()) {
                FileWriter writer = new FileWriter(TRACKER_PATH);
                writer.write("00");
                writer.close();
            }

            String[] splitted = Files.readString(Path.of(TRACKER_PATH)).split("");
            int numPlayers = Integer.parseInt(splitted[0]) + 1;
            FileWriter myWriter = new FileWriter(TRACKER_PATH);
            myWriter.write(numPlayers + splitted[1]);
            myWriter.close();

            // This also creates the storage file - needs to be refactored

            AnchorPane root = FXMLLoader.load(getClass().getResource(FXML_PATH + "MainMenu.fxml"));

            // Play the start video
            Logger.debug("Initialising Start Video");
            MediaPlayer mediaPlayer = new MediaPlayer(new Media(getClass().getResource(VIDEO_PATH + "startvid.mp4").toExternalForm()));
            mediaPlayer.setVolume(FileHandler.getMusicVolume());
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(X_SCREEN_WIDTH);
            mediaView.setFitHeight(Y_SCREEN_WIDTH);
            root.getChildren().add(mediaView);
            mediaPlayer.setOnEndOfMedia((() -> {
                mediaPlayer.stop();
                mediaView.setOpacity(0);
                mediaView.setDisable(true);
                Logger.debug("Ending Start Video Playback");
                AudioController.startMusic("menuMusic.mp3");
            }));
            mediaPlayer.play();

            Scene scene = new Scene(root, 1550, 800);
            scene.getStylesheets().add(getClass().getResource(CSS_PATH + "style.css").toExternalForm());
            primaryStage.setScene(scene);

            // Allows skipping of the intro video when the spacebar, enter key or escape key is pressed
            /*
            scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
                KeyCode keyCode = keyEvent.getCode();
                if(keyCode.equals(KeyCode.SPACE) || keyCode.equals(KeyCode.ENTER) || keyCode.equals(KeyCode.ESCAPE)){
                    mediaView.setVisible(false);
                    mediaPlayer.setVolume(0);
                }
                keyEvent.consume();
            });
            */
            
            //tells you what is equipped
            Logger.debug("The current equipped snake skin is " + new SnakeSkinManager().getSelectedSkin());
            Logger.debug("The current equipped map skin is " + new MapSkinManager().getSelectedMap());
            /* I commented out image loading prints to console to make debugging easier,
             * if needed, uncomment from MapSkin.java
             * located at: /paradroid/client/src/main/server/game/managers/mapmanager/MapSkin.java
             */

            primaryStage.centerOnScreen();
            primaryStage.show();
            primaryStage.setResizable(false);
            primaryStage.setTitle(WINDOW_TITLE);

            //used to update the tracker txt file when pressing x to close the game. Important for shop multiplayer functionality.
            primaryStage.setOnCloseRequest(we -> {
                Logger.debug("Updating tracker.txt");

                try {
                    String[] splitted1 = Files.readString(Path.of(TRACKER_PATH)).split("");
                    int numPlayers1 = Integer.parseInt(splitted1[0]) - 1;
                    FileWriter myWriter1 = new FileWriter(TRACKER_PATH);
                    myWriter1.write(numPlayers1 + "0");
                    myWriter1.close();
                } catch (IOException e) {
                    Logger.error("Error writing to tracker file");
                    e.printStackTrace();
                }

            });

        } catch (IOException e) {
            Logger.error("Error while starting the client");
            e.printStackTrace();
        }

    }
}