package server.game.managers.powerupsmanager;

import server.game.managers.mapmanager.Map;

/**
 * A powerup which freezes a player temporarily
 */
public class Freeze {

    private final Map map;
    private int freezeTimer;

    /**
     * @param map the map to use
     */
    public Freeze(Map map) {

        this.map = map;
        this.freezeTimer = 0;
    }

    public int getFreezeTimer() {

        return freezeTimer;
    }

    public void setFreezeTimer(int newFreeze) {

        this.freezeTimer = newFreeze;
    }
}
