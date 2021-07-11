package handlers;

import interfaces.PathConstants;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AudioSettings implements Initializable, PathConstants {

    @FXML
    public CheckBox mute;
    @FXML
    public Slider musicVolume;
    @FXML
    public Slider effectVolume;

    /**
     * Initialises audio settings
     *
     * @param unusedUrl - unusedRb, needed for override
     * @param unusedRb  - unusedRb, needed for override
     */
    @Override
    public void initialize(URL unusedUrl, ResourceBundle unusedRb) {

        effectVolume.setValue(FileHandler.getEffectVolume());

        musicVolume.setValue(AudioController.getMusicVolume() * 100);
        musicVolume.valueProperty().addListener(arg01 -> {
            AudioController.setMusicVolume(musicVolume.getValue() / 100);

            if (mute.isSelected()) {
                if (musicVolume.getValue() > 0 || effectVolume.getValue() > 0) {
                    mute.setSelected(false);
                    FileHandler.saveMuteStatus(false);
                }

            }
            String getMuteString = FileHandler.getMuteStatus();
            if (getMuteString.equals("False")) {
                mute.setSelected(false);
            }

            if (getMuteString.equals("True")) {
                mute.setSelected(true);
            }
        });

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
     * Called by fxml - toggles mute
     */
    public void setMute() {

        if (mute.isSelected()) {
            musicVolume.setValue(0);
            effectVolume.setValue(0);
            FileHandler.saveMuteStatus(true);
            mute.setSelected(true);
        } else if (!mute.isSelected()) {
            FileHandler.saveMuteStatus(false);
            musicVolume.setValue(FileHandler.getMusicVolume());
            effectVolume.setValue(FileHandler.getMusicVolume());
            mute.setSelected(false);
        }

    }

    /**
     * Called by fxml - set's default volume at 50%
     */
    public void setDefaultVolume() {

        AudioController.clickSound();
        musicVolume.setValue(50);
        effectVolume.setValue(50);
    }

    /**
     * Called by fxml - writes the current volume to a file
     */
    public void applyVolume() {

        String path = EFFECTS_PATH + "uiClick.mp3";
        Media media = new Media(new File(path).toURI().toString());
        MediaPlayer m = new MediaPlayer(media);
        m.setVolume(effectVolume.getValue() / 100); // get the value from slider
        m.play();

        FileHandler.saveAudioSettings(musicVolume.getValue(), effectVolume.getValue());

    }

}
