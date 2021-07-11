package server.game.managers.powerupsmanager;

import server.game.managers.mapmanager.Map;

/**
 * A powerup allowing a player to skip through walls
 */
public class WallSkipper {

    private final Map map;
    private int skipTimer;

    /**
     * @param map the map to use
     */
    public WallSkipper(Map map) {

        this.map = map;
        this.skipTimer = 0;
    }

    public int getSkipTimer() {

        return skipTimer;
    }

    public void setSkipTimer(int newSkipper) {

        this.skipTimer = newSkipper;
    }
}
