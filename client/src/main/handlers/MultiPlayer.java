package handlers;

import interfaces.GameConstants;
import interfaces.PathConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logging.Logger;
import server.game.managers.mapmanager.Map;
import server.game.managers.mapmanager.MapManager;
import server.networking.Client;
import server.networking.Server;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The MultiPlayer class is responsible for handling actions made on the MultiPlayer UI, as well as starting the server and clients.
 * It handles choices made by the host such as the map choice or number of lives, and then communicates these choices with the server.
 */

public class MultiPlayer implements PathConstants, GameConstants {

    @FXML
    private Button createGame;
    @FXML
    private Button joinGame;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private ChoiceBox<String> livesChoice;
    @FXML
    private Text loading;

    private int numLives = NO_OF_LIVES;
    private Map mapValue;

    private Boolean serverAlreadyRunning() {

        String[] splitted = null;
        try {
            splitted = Files.readString(Path.of(TRACKER_PATH)).split("");
        } catch (IOException e) {
            Logger.debug("Error reading from tracker file");
        }
        return (!splitted[1].equals("0"));
    }

    private void createLivesChoices() {

        for (int j = 1; j < 6; j++) {
            livesChoice.getItems().add(String.valueOf(j));
        }
        livesChoice.setValue("Number of Lives To Begin With");
    }

    private void getMap() {

        createMapChoices();
    }

    private void createMapChoices() {

        MapManager mm = new MapManager(30, 30);
        String[] array = mm.getAllFileNames().toArray(new String[0]);
        for (String s : array) {
            choiceBox.getItems().add(s);
        }
        choiceBox.getItems().add("Random Map");
        choiceBox.setValue("Choose Custom Map");
    }

    /**
     * Method to display the main menu when the back button is pressed
     *
     * @param event the ActionEvent of the button being pressed
     */

    public void getMain(ActionEvent event) {

        try {
            String[] splitted = Files.readString(Path.of(TRACKER_PATH)).split("");
            FileWriter myWriter = new FileWriter(TRACKER_PATH);
            myWriter.write(splitted[0] + "0");
            myWriter.close();
            Server.turnOffServer();
        } catch (Exception e) {
            Logger.error("Error writing to tracker file");
        }
        Util.getWindow(event, "MainMenu");
    }

    /**
     * Method thats called when a user hosts a game , allowing them to choose the number of lives both players should start the game with
     * Ensures that there isn't a server already running
     */

    public void chooseLives() {

        AudioController.clickSound();
        if (!serverAlreadyRunning()) {
            choiceBox.setDisable(true);
            joinGame.setDisable(true);
            createGame.setDisable(true);
            createLivesChoices();
        }
    }

    /**
     * Method that stores the number of lives the host has chosen and then calls a method to allow the host to choose a map
     */

    public void setLives() {

        AudioController.clickSound();
        String value = livesChoice.getValue();
        for (int i = 0; i < livesChoice.getItems().size(); i++) {
            if (livesChoice.getItems().get(i).equals(value)) {
                numLives = i + 1;
                choiceBox.setDisable(false);
                livesChoice.setDisable(true);
                getMap();
            }
        }
    }

    /**
     * Method that starts the server with the hosts chosen map and number of lives. It then starts a client.
     *
     * @param event the ActionEvent of the button being pressed
     */

    public void getServer(ActionEvent event) {

        AudioController.clickSound();
        MapManager mm = new MapManager(30, 30);
        String value = choiceBox.getValue();
        for (int i = 0; i < choiceBox.getItems().size(); i++) {
            if (choiceBox.getItems().get(i).equals(value)) {
                if (i == choiceBox.getItems().size() - 1) {
                    mapValue = new Map(30, 30);
                } else mapValue = mm.getMap(i);

            }
        }

        if (mapValue != null) {
            choiceBox.setDisable(true);
            loading.setVisible(true);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Thread serverThread = new Thread(() -> Server.main(mapValue, numLives));

            serverThread.start();

            Thread clientThread = new Thread(() -> Client.main(window));

            clientThread.start();
        }
    }

    /**
     * Method that calls the client class given that the server is already running
     *
     * @param event the ActionEvent of the button being pressed
     */

    public void getClient(ActionEvent event) {

        AudioController.clickSound();

        int activeServers = 0;

        try {
            String[] splitted = Files.readString(Path.of(TRACKER_PATH)).split("");
            activeServers = Integer.parseInt(splitted[1]);
        } catch (Exception e) {
            Logger.error("Error writing to tracker file");
        }

        if (activeServers >= 1) {
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Thread clientThread = new Thread(() -> Client.main(window));

            clientThread.start();
        }
    }

}