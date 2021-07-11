package server.game;

import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.usables.Coordinate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a wall
 */
public class Wall {

    private final List<Coordinate> wallsCoordinates;

    /**
     * @param map the map to use
     */
    public Wall(Map map) {

        wallsCoordinates = findWalls(map);
    }

    /* Scans the given map for Walls (written as "/" in the array) and adds the coordinates
        to an ArrayList */
    private List<Coordinate> findWalls(Map map) {

        BoxStatus[][] mapMatrix = map.getMap();
        ArrayList<Coordinate> walls = new ArrayList<>();

        for (int x = 0; x < map.getX(); x++) {

            for (int y = 0; y < map.getY(); y++) {

                if (mapMatrix[x][y].equals(BoxStatus.WALL)) {

                    walls.add(new Coordinate(x, y));
                }
            }
        }

        return walls;
    }

    /**
     * @return the coordinates of walls on the map
     * @throws IOException if there are no walls on the map
     */
    public List<Coordinate> getCoordinates() throws IOException {

        if (wallsCoordinates.isEmpty()) {

            throw new IOException("No walls on the map or no coordinates registered.");
        }

        return wallsCoordinates;
    }
}
