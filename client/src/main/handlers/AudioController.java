package handlers;

import interfaces.PathConstants;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.Random;

/**
 * Handles sound effects and music
 */
public class AudioController implements PathConstants {

    private static MediaPlayer mediaPlayer;
    private static MediaPlayer effectsPlayer;

    /**
     * Begins playback of media
     *
     * @param songName the name of the song, e.g "menuMusic.mp3"
     */
    public static void startMusic(String songName) {

        FileHandler.createFile();
        FileHandler2.FileCreation();

        File file = new File(MUSIC_PATH + songName);
        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.seek(new Duration(1000 * 53));
        mediaPlayer.setVolume(FileHandler.getMusicVolume() / 100);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        });
        mediaPlayer.play();
    }

    /**
     * Ends playback of media
     */
    public static void endMusic() {
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
    }

    public static void clickSound() {

        playSoundEffect("uiClick.mp3");
    }

    public static void hitWallSound() {

        playSoundEffect("hitWall.mp3");
    }

    public static void collectCoinSound() {

        playSoundEffect("collectCoin.mp3");
    }

    public static void collectSound() {

        playSoundEffect("collect.mp3");
    }

    public static void consumeSound() {

        playSoundEffect("consume.mp3");
    }

    public static void deathSound() {

        playSoundEffect("death.mp3");
    }

    public static void powerupSound() {

        playSoundEffect("usePowerup.mp3");
    }

    public static void countDownSound() {

        playSoundEffect("countdownBeep.mp3");
    }

    public static void gameStartSound() {
        // Sound is no longer needed but left in incase of future use
        //playSoundEffect("gameStart.wav");
    }

    /**
     * Set the effect player volume
     * @param volume the value to set the volume to
     */
    public static void setEffectVolume(double volume){
        effectsPlayer.setVolume(volume);
    }

    /**
     * Set the music player volume
     * @param volume the value to set the volume to
     */
    public static void setMusicVolume(double volume){
        mediaPlayer.setVolume(volume);
    }

    /**
     * @return the current effect volume
     */
    public static double getEffectVolume(){
        return effectsPlayer.getVolume();
    }

    /**
     * @return the current music volume
     */
    public static double getMusicVolume(){
        return mediaPlayer.getVolume();
    }

    /**
     * Loads and plays an effect at a specified path
     *
     * @param soundPath the local path of the file (e.g "uiClick.mp3")
     */
    private static void playSoundEffect(String soundPath) {

        Media media = new Media(new File(EFFECTS_PATH + soundPath).toURI().toString());
        effectsPlayer = new MediaPlayer(media);
        effectsPlayer.setVolume(FileHandler.getEffectVolume() / 100); // get the value from slider
        effectsPlayer.play();
    }

    /**
     * Loads a random song from the music directory
     */
    private static File getRandomSongFile() {

        File folder = new File(MUSIC_PATH);
        File[] files = folder.listFiles();

        int randomIndex = new Random().nextInt(files.length);
        return files[randomIndex];
    }

}
