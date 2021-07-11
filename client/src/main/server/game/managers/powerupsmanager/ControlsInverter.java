package server.game.managers.powerupsmanager;

import server.game.managers.mapmanager.Map;

/**
 * A powerup which inverts controls of a player
 */
public class ControlsInverter {

    private final Map map;
    private int invertTimer;

    /**
     * @param map The map to use
     */
    public ControlsInverter(Map map) {

        this.map = map;
        this.invertTimer = 0;
    }

    public int getInvertTimer() {

        return invertTimer;
    }

    public void setInvertTimer(int newInvert) {

        invertTimer = newInvert;
       // SinglePlayer.powerUpImage.setImage(IMAGE_PATH+life.png);
    }
}
