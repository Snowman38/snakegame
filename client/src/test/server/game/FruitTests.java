package server.game;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.Test;
import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.usables.Coordinate;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FruitTests {

    JFXPanel panel = new JFXPanel();

    Map map = new Map(30, 30, new File("client/src/test/server/game/managers/mapmanager/TestMap.txt"));
    Fruit fruit = new Fruit(map);

    @Test
    public void testSpawnFruit() {

        fruit.spawnRandomFruit(map);

        assertTrue(fruit.getFruitPos().getX() < 30 && fruit.getFruitPos().getX() > 0 &&
                fruit.getFruitPos().getY() < 30 && fruit.getFruitPos().getY() > 0);

        assertTrue(map.getMapValue(fruit.getFruitPos()).equals(BoxStatus.FRUIT));
    }

    @Test
    public void testDespawnFruit() {

        Coordinate coord = fruit.getFruitPos();
        fruit.despawnFruit(map);

        assertNull(fruit.getFruitPos());
        assertTrue(map.getMapValue(coord).equals(BoxStatus.EMPTY));
    }
}
