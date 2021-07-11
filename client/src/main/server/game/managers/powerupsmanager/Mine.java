package server.game.managers.powerupsmanager;

import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.usables.Coordinate;

/**
 * A powerup which allows a player to drop a mine
 */
public class Mine {

    private final Map map;
    private Coordinate mineCoordinate;

    /**
     * @param map the map to use
     */
    public Mine(Map map) {

        this.map = map;
    }

    /**
     * Plants a mine at the specified coordinate
     *
     * @param coordinate the location to place the mine
     */
    public void plantMine(Coordinate coordinate) {

        map.setMapValue(coordinate, BoxStatus.PLACED_MINE);
        this.mineCoordinate = coordinate;
    }

    /**
     * Removes a mine from the map
     */
    public void despawnMine() {

        map.setMapValue(this.mineCoordinate, BoxStatus.EMPTY);
    }

    public Coordinate getMineCoordinate() {

        return this.mineCoordinate;
    }
}
