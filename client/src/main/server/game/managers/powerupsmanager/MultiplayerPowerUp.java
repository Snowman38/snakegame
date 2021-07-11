package server.game.managers.powerupsmanager;

import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.usables.Coordinate;

import java.io.Serializable;
import java.util.Random;

/**
 * A class to manage powerups
 */
public class MultiplayerPowerUp implements Serializable {

    private Coordinate coordinates;
    private Map map;
    private BoxStatus powerType;

    /**
     * @param map the map to use
     */
    public MultiplayerPowerUp(Map map) {

        this.getRandomPowerUp();
        spawnPowerUp(map);
    }

    /**
     * Spawns a random powerup, with it's type decided by a weighting
     */
    private void getRandomPowerUp() {

        Random rand = new Random();
        int randomNo = rand.nextInt(49);

        if (randomNo < 9) {
            powerType = BoxStatus.MINE;
        } else if (randomNo < 18) {
            powerType = BoxStatus.FREEZE;
        } else if (randomNo < 27) {
            powerType = BoxStatus.SKIP;
        } else if (randomNo >= 36 && randomNo < 45) {
            powerType = BoxStatus.SPEED;
        } else if (randomNo >= 45 && randomNo < 48) {
            powerType = BoxStatus.REVERSE;
        } else {
            powerType = BoxStatus.COIN;
        }
    }

    /**
     * @return the type of powerup
     */
    public BoxStatus getPowerType() {

        return powerType;
    }

    /**
     * Spawns a powerup in a random location on the map
     *
     * @param map the map to use
     */
    public void spawnPowerUp(Map map) {

        boolean spawned = false;
        Random rand = new Random();

        while (!spawned) {

            Coordinate randomCoord = new Coordinate(rand.nextInt(map.getX()), rand.nextInt(map.getY()));

            if (map.getMapValue(randomCoord).equals(BoxStatus.EMPTY)) {

                this.coordinates = randomCoord;
                this.getRandomPowerUp();
                map.setMapValue(randomCoord, powerType);
                spawned = true;
            }
        }
    }

    /**
     * Removes a powerup from the map
     *
     * @param map the map to use
     */
    public void despawnPowerUp(Map map) {

        if (coordinates != null) {

            map.setMapValue(coordinates, BoxStatus.EMPTY);
            coordinates = null;
        }
    }

    public Coordinate getCoordinate() {

        return this.coordinates;
    }
}
