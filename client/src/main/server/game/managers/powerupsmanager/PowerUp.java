package server.game.managers.powerupsmanager;

import java.io.Serializable;
import java.util.Random;

import handlers.SinglePlayer;
import interfaces.PathConstants;
import javafx.scene.image.Image;
import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.usables.Coordinate;

/**
 * A class to manage powerups
 */
public class PowerUp implements Serializable, PathConstants {

    private Coordinate coordinates;
    private Map map;
    private BoxStatus powerType;

    private final Image mine = new Image(IMAGE_PATH + "mine.png");
    private final Image freeze = new Image(IMAGE_PATH + "freeze.png");
    private final Image skip = new Image(IMAGE_PATH + "skip.png");
    private final Image speed = new Image(IMAGE_PATH + "speed.png");
    private final Image reverse = new Image(IMAGE_PATH + "reverse.png");


    /**
     * @param map the map to use
     */
    public PowerUp(Map map) {

        this.getRandomPowerUp();
        spawnPowerUp(map);
    }

    public PowerUp(Map map, Coordinate newCoord) {
        this.getRandomPowerUp();
        this.coordinates = newCoord;
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

            if (getPowerType()==BoxStatus.MINE) {
                SinglePlayer.updatePowerUpBox(mine);
            }

            if (getPowerType()==BoxStatus.FREEZE) {
                SinglePlayer.updatePowerUpBox(freeze);
            }

            if (getPowerType()==BoxStatus.SKIP) {
                SinglePlayer.updatePowerUpBox(skip);
            }

            if (getPowerType()==BoxStatus.SPEED) {
                SinglePlayer.updatePowerUpBox(speed);
            }

            if (getPowerType()==BoxStatus.REVERSE) {
                SinglePlayer.updatePowerUpBox(reverse);
            }

        }
    }
    
    /**
     * Removes a powerup from the map
     *
     * @param map the map to use
     */
    public void despawnPowerUp2(Map map) {

        if (coordinates != null) {

            map.setMapValue(coordinates, this.powerType);
            
        }
    }

    public Coordinate getCoordinate() {

        return this.coordinates;
    }

    /**
     * @return the type of powerup
     */
    public BoxStatus getPowerType() {

        return this.powerType;
    }
}