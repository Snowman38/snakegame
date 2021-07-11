package server.game.managers.mapmanager;

import static org.junit.jupiter.api.Assertions.*;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;

import java.io.File;

import server.game.usables.*;

public class MapTests {

    JFXPanel panel = new JFXPanel();

    Map map = new Map(30, 30, new File("client/src/test/server/game/managers/mapmanager/TestMap.txt"));

    @Test
    public void testMapFileRead() {

        assertEquals(30, map.getX());
        assertEquals(30, map.getY());
        assertEquals(BoxStatus.WALL, map.getMapValue(0, 0));
        assertEquals(BoxStatus.WALL, map.getMapValue(0, 1));
        assertEquals(BoxStatus.WALL, map.getMapValue(0, 2));

    }

    @Test
    public void testGenerateMapAndGenerateRandomWalls() {

        map = new Map(50, 60);
        assertEquals(50, map.getX());
        assertEquals(60, map.getY());

        int wallCount = 0;

        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 60; y++) {
                BoxStatus status = map.getMapValue(x, y);
                if (status.equals(BoxStatus.WALL)) {
                    wallCount++;
                }
            }
        }

        assertTrue(wallCount < 100 && wallCount > 60);
    }

    @Test
    public void testGetX() throws Exception {

        map.setX(10);
        assertEquals(map.getX(), 10);
    }

    @Test
    public void testGetY() throws Exception {

        map.setY(10);
        assertEquals(map.getY(), 10);
    }

    @Test
    public void testSetXA() throws Exception {

        map.setX(5);
        assertEquals(map.getX(), 5);
    }

    @Test
    public void testSetXB() {

        assertThrows(Exception.class,
                () -> map.setX(-2),
                "Expected Exception on wrong input.");
    }

    @Test
    public void testSetY() throws Exception {

        map.setY(5);
        assertEquals(map.getY(), 5);
    }

    @Test
    public void testSetYB() {

        assertThrows(Exception.class,
                () -> map.setY(-2),
                "Expected Exception on wrong input.");
    }

    @Test
    public void testGetMapValue() {

        map.setMapValue(new Coordinate(0, 0), BoxStatus.WALL);
        assertEquals(map.getMapValue(new Coordinate(0, 0)), BoxStatus.WALL);

        map.setMapValue(new Coordinate(0, 3), BoxStatus.PLACED_MINE);
        assertEquals(map.getMapValue(new Coordinate(0, 3)), BoxStatus.PLACED_MINE);
    }

    @Test
    public void testSetMapValue() {

        map.setMapValue(new Coordinate(3, 5), BoxStatus.COIN);
        assertEquals(map.getMapValue(new Coordinate(3, 5)), BoxStatus.COIN);

        map.setMapValue(new Coordinate(4, 2), BoxStatus.FRUIT);
        assertEquals(map.getMapValue(new Coordinate(4, 2)), BoxStatus.FRUIT);
    }

    @Test
    public void testGetMap() {

        BoxStatus[][] matrix = map.getMap();

        assertEquals(map.getMapValue(new Coordinate(0, 2)), matrix[0][2]);
        assertEquals(map.getMapValue(new Coordinate(12, 8)), matrix[12][8]);
        assertEquals(map.getMapValue(new Coordinate(18, 22)), matrix[18][22]);
    }

    @Test
    public void testSetMatrix() {

        Map testMap = new Map(30, 30);

        map.setMatrix(testMap.getMap());

        assertEquals(map.getMapValue(new Coordinate(14, 28)), testMap.getMapValue(new Coordinate(14, 28)));
        assertEquals(map.getMapValue(new Coordinate(6, 0)), testMap.getMapValue(new Coordinate(6, 0)));
        assertEquals(map.getMapValue(new Coordinate(19, 24)), testMap.getMapValue(new Coordinate(19, 24)));
    }
}
