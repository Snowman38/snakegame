package handlers;

import interfaces.PathConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logging.Logger;

import java.io.IOException;

/**
 * Utility class for repetitive functions
 */
public class Util implements PathConstants {

    /**
     * Switches the stage on the screen. It set's stylesheets and fxml references, then sets parents and displays the window
     *
     * @param event      the calling ActionEvent (Called by FXML)
     * @param windowName The name of the fmxl window (e.g mainMenu, singleplayer, shop)
     */
    public static void getWindow(ActionEvent event, String windowName) {
        Scene scene = ((Node) event.getSource()).getScene();
        initWindow(scene, windowName);
    }

    /**
     * Switches the stage on the screen. It set's stylesheets and fxml references, then sets parents and displays the window
     *
     * @param scene      the calling scene
     * @param windowName The name of the fmxl window (e.g mainMenu, singleplayer, shop)
     */
    public static void getWindow(Scene scene, String windowName) {
        initWindow(scene, windowName);
    }

    private static void initWindow(Scene scene, String windowName){
        AudioController.clickSound();

        try {
            Parent parent = FXMLLoader.load(Util.class.getResource(FXML_PATH + windowName + ".fxml"));
            parent.getStylesheets().add(Util.class.getResource(CSS_PATH + "style.css").toExternalForm());
            Scene parentScene = new Scene(parent);

            Stage window = (Stage) scene.getWindow();
            window.setScene(parentScene);
            window.show();
        } catch (IOException e) {
            Logger.error("Error getting window " + windowName);
            e.printStackTrace();
        }
    }
}
