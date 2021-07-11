package handlers;

import interfaces.PathConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import logging.Logger;

import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

/**
 * The main menu of the application
 */
public class MainMenu implements Initializable, PathConstants {

    static int ID;
    static boolean initialized = false;

    @FXML
    Label balance;

    @FXML
    Button singlePlayer;

    /**
     * Override method initialising the balance and player id's
     *
     * @param url            unused
     * @param resourceBundle unused
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            if (!initialized) {

                String[] splitted = Files.readString(Path.of(TRACKER_PATH)).split("");
                int numPlayers = Integer.parseInt(splitted[0]);
                ID = numPlayers;
                initialized = true;
            }
            if (ID == 1) {
                balance.setText(Integer.toString(FileHandler.getBalance()));
            } else {
                balance.setText(Integer.toString(FileHandler2.getBalance()));
            }

        } catch (Exception e) {
            Logger.error("Error reading tracker file");
        }
    }

    /**
     * Returns the audio window
     *
     * @param event ActionEvent called from fxml
     */
    public void getAudio(ActionEvent event) {

        Util.getWindow(event, "AudioSettings");
    }

    /**
     * Returns the singleplayer window
     *
     * @param event ActionEvent called from fxml
     */
    public void getSinglePlayer(ActionEvent event) {

        Util.getWindow(event, "SinglePlayer");
    }

    /**
     * Returns the multiplayer window
     *
     * @param event ActionEvent called from fxml
     */
    public void getMultiPlayer(ActionEvent event) {

        Util.getWindow(event, "MultiPlayer");
    }

    /**
     * Returns the build a map window
     *
     * @param event ActionEvent called from fxml
     */
    public void getBuildAMap(ActionEvent event) {

        Util.getWindow(event, "BuildAMap");
    }

    /**
     * Returns the shop window
     *
     * @param event ActionEvent called from fxml
     */
    public void getShop(ActionEvent event) {

        try {
            if (ID == 1) {
                Util.getWindow(event, "Shop");
            } else {
                Util.getWindow(event, "Shop2");
            }
        } catch (Exception e) {
            Logger.error("Error getting shop");
        }

    }

    /**
     * Handles the close button press
     *
     * @param event ActionEvent called from fxml
     */
    @FXML
    public void handleCloseButtonAction(ActionEvent event) {

        try {
            String[] splitted = Files.readString(Path.of(TRACKER_PATH)).split("");
            int numPlayers = Integer.parseInt(splitted[0]) - 1;
            FileWriter myWriter = new FileWriter(TRACKER_PATH);
            myWriter.write(numPlayers + String.valueOf(splitted[1]));
            myWriter.close();
        } catch (Exception e) {
            Logger.error("Error writing to tracker file");
        }
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

}