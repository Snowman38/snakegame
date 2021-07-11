package handlers;

import interfaces.PathConstants;
import interfaces.GameConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import logging.Logger;
import server.game.managers.mapmanager.MapSkinManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 * A map building class allowing a user to click on squares to build a map, then save it to a file
 */
public class BuildAMap implements PathConstants, GameConstants {

    // new GridPane() has to be here for some reason or the code breaks
    @FXML
    GridPane gridPane = new GridPane();
    Button[][] buttons;
    @FXML
    TextField mapName;
    @FXML
    Label success;
    String[][] buttonStatus;
    @FXML
    ChoiceBox mapChoice;
    private MapSkinManager msm;
    private int gridPaneX;
    private int gridPaneY;

    // Mirrors a n x n array into a 2n x 2n array, mirrored around the x y origin. NOT USED ANYMORE
    private String[][] mirrorStringArray(String[][] array) {

        String[][] fullArray = new String[MAP_CELLS_X][MAP_CELLS_Y];
        int fullWidthX = 2 * array.length;
        int fullWidthY = 2 * array[0].length;
        for (int x = 0; x < gridPaneX; x++) {
            for (int y = 0; y < gridPaneY; y++) {
                String s = array[x][y];
                fullArray[x][y] = s;
                fullArray[fullWidthX - 1 - x][y] = s;
                fullArray[x][fullWidthY - 1 - y] = s;
                fullArray[fullWidthX - 1 - x][fullWidthY - 1 - y] = s;
            }
        }
        return fullArray;
    }

    // Converts an array of strings into a string seperated by spaces and \n's
    private String stringArrayToString(String[][] array) {

        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[0].length; y++) {
                sb.append(array[x][y]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * The "Constructor" for this class
     */
    @FXML
    public void initialize() {

        msm = new MapSkinManager();

        try {
            Image backgroundSkin = msm.getMapSkin(msm.getSelectedMap()).getBackgroundSkin();
            Image wallSkin = msm.getMapSkin(msm.getSelectedMap()).getWallSkin();

            this.gridPaneX = MAP_CELLS_X;
            this.gridPaneY = MAP_CELLS_Y;

            buttonStatus = new String[gridPaneX][gridPaneY];

            // Initialise each string as 0
            for (String[] row : buttonStatus)
                Arrays.fill(row, "0");

            // Clear the gridpane and initialise a new button array
            this.gridPane.getChildren().clear();
            this.buttons = new Button[gridPaneX][gridPaneY];

            // CSS styling of a button inside the map builder
            String buttonStyle = "-fx-background-color: transparent ; -fx-blend-mode: SRC_OVER; -fx-font-size: 0; -fx-font-color:transparent;";

            // Populates the grid of buttons with Button objects
            for (int x = 0; x < gridPaneX; x++) {
                for (int y = 0; y < gridPaneY; y++) {

                    Button button = new Button();
                    button.setStyle(buttonStyle);

                    ImageView background = new ImageView(backgroundSkin);

                    background.setFitHeight(this.gridPane.getPrefHeight() / gridPaneX);
                    background.setPreserveRatio(true);
                    button.setGraphic(background);

                    // Arbitrarily large button size such that each button is the size of it's parent container
                    button.setMaxSize(50, 50);

                    int x1 = x;
                    int y1 = y;

                    button.setOnAction(event -> {
                        if (button.getGraphic() == background) {
                            ImageView wall = new ImageView(wallSkin);

                            wall.setFitHeight(this.gridPane.getPrefHeight() / 30);
                            wall.setPreserveRatio(true);
                            button.setGraphic(wall);

                            buttonStatus[x1][y1] = "/";

                        } else {
                            button.setGraphic(background);
                            buttonStatus[x1][y1] = "0";
                        }
                    });

                    buttons[x][y] = button;
                    gridPane.add(button, y, x);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the main menu
     *
     * @param event The calling ActionEvent
     */
    public void getMain(ActionEvent event) {

        Util.getWindow(event, "MainMenu");
    }

    /**
     * Saves the current button map to a file
     */
    public void saveToFile() {
        // Creating a file
        try {
            File file = new File(MAP_PATH + mapName.getText() + ".txt");
            if (file.createNewFile()) {
                success.setText("Your map was saved successfully");
                Logger.debug("File created: " + file.getName());
            } else {
                success.setText("You already have a map saved with this name");
                Logger.debug("File already exists.");
            }
        } catch (IOException e) {
            Logger.error("Map wasn't created as saveToFile() couldn't create a new file.");
            e.printStackTrace();
            return;
        }

        // Writing to the file
        try {
            FileWriter fileWriter = new FileWriter(MAP_PATH + mapName.getText() + ".txt");
            String[][] mapStringArray = this.buttonStatus;
            fileWriter.write(stringArrayToString(mapStringArray));
            fileWriter.close();
            Logger.debug("Map has been written to file.");
        } catch (IOException e) {
            Logger.error("Map wasn't written to file.");
            e.printStackTrace();
        }
    }

}
