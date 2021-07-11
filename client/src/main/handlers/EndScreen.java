package handlers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class EndScreen {

    /**
     * Loads the main menu
     *
     * @param event the calling ActionEvent (Called from FXML)
     */
    public void getMain(ActionEvent event){

        Util.getWindow(event, "MainMenu");
    }

}
