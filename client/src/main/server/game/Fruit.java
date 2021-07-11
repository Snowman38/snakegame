package server.game;

import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.usables.Coordinate;

import java.util.Random;

/**
 * A class representing a fruit object
 */
public class Fruit {

    private final Random random = new Random();
    private Coordinate coordinates;

    /**
     * Constructor which spawns a fruit in a random location on the map
     *
     * @param map the selected map
     */
    public Fruit(Map map) {

        spawnRandomFruit(map);
    }

    public Fruit(Map map, Coordinate newCoord) {
        this.coordinates = newCoord;
    }

    /**
     * @return the Coordinate of this fruit object
     */
    public Coordinate getFruitPos() {

        return coordinates;
    }

    /**
     * Spawns a random food object that is not on top of a snake, wall or another food object
     *
     * @param map the map to use
     */
    public void spawnRandomFruit(Map map) {

        boolean spawned = false;

        while (!spawned) {

            Coordinate randomCoord = new Coordinate(random.nextInt(map.getX()), random.nextInt(map.getY()));

            if (map.getMapValue(randomCoord).equals(BoxStatus.EMPTY)) {
                this.coordinates = randomCoord;
                map.setMapValue(randomCoord, BoxStatus.FRUIT);
                spawned = true;
            }
        }
    }

    /**
     * Despawns a food object (used for when a snake eats said food object)
     *
     * @param map the map to use
     */
    public void despawnFruit(Map map) {

        map.setMapValue(coordinates, BoxStatus.EMPTY);
        coordinates = null;
    }
}
