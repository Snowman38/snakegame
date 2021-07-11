package server.game.managers.snakemanager;

import static org.junit.jupiter.api.Assertions.*;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.ai.Difficulty;

import server.game.Fruit;
import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.managers.powerupsmanager.PowerUp;
import server.game.usables.*;


import java.io.File;
import java.io.IOException;

public class SnakeTests {

    Map map = new Map(30, 30, new File("client/src/test/server/game/managers/mapmanager/TestMap.txt"));
    SnakeSkinManager ssm;
    Snake snake;
    PlayerSnake playerSnake;
    AISnake aiSnake;
    JFXPanel panel = new JFXPanel();

    @BeforeEach
    public void setUp() {

        ssm = new SnakeSkinManager();
        snake = new Snake(1, map, ssm.getSnakeSkin(1), 5);
        playerSnake = new PlayerSnake(1, map, ssm.getSnakeSkin(0), 5);
        aiSnake = new AISnake(1, map, ssm.getSnakeSkin(1), 5, Difficulty.MEDIUM);
    }

    @Test
    public void testSpawnSnake() {

        snake.spawnSnake();

        assertTrue(snake.getStartPoint().getX() < 30 && snake.getStartPoint().getX() >= 0 &&
                snake.getStartPoint().getY() < 30 && snake.getStartPoint().getY() >= 0 &&
                map.getMapValue(snake.getStartPoint()).equals(BoxStatus.EMPTY));
    }

    @Test
    public void testChangeSpawn() {

        snake.changeSpawn();
        testSpawnSnake();
    }

    @Test
    public void testUpdatePlayerSnakeA() {

        Coordinate nextCoord = new Coordinate(playerSnake.getHeadPoint().getX(), playerSnake.getHeadPoint().getY() - 1);
        map.setMapValue(nextCoord, BoxStatus.EMPTY);
        playerSnake.updateSnake();

        assertEquals(playerSnake.getStartPoint().getY(), playerSnake.getHeadPoint().getY() + 1);
        assertEquals(playerSnake.getStartPoint().getX(), playerSnake.getHeadPoint().getX());
    }

    @Test
    public void testUpdatePlayerSnakeB() {

        Coordinate nextCoord = new Coordinate(playerSnake.getHeadPoint().getX(), playerSnake.getHeadPoint().getY() - 1);
        map.setMapValue(nextCoord, BoxStatus.WALL);

        playerSnake.updateSnake();

        assertEquals(playerSnake.getStartPoint(), playerSnake.getHeadPoint());
    }

    @Test
    public void testHandleFruitA() throws IOException {

        int currentLength = playerSnake.getLength();
        Coordinate nextCoord = new Coordinate(playerSnake.getHeadPoint().getX(), playerSnake.getHeadPoint().getY() - 1);
        Fruit fruit = new Fruit(map, nextCoord);

        playerSnake.updateSnake();
        playerSnake.handleFruit(fruit, aiSnake);
        playerSnake.updateSnake();

        assertEquals(playerSnake.getLength(), currentLength + 1);
        assertEquals(aiSnake.getLength(), currentLength - 1);
        assertNotEquals(fruit.getFruitPos(), nextCoord);
    }

    @Test
    public void testHandleFruitB() throws IOException {

        int currentLength = playerSnake.getLength();
        Coordinate fruitCoord = new Coordinate(playerSnake.getHeadPoint().getX() + 1, playerSnake.getHeadPoint().getY() + 1);
        Fruit fruit = new Fruit(map, fruitCoord);

        playerSnake.updateSnake();
        playerSnake.handleFruit(fruit, aiSnake);
        playerSnake.updateSnake();

        assertEquals(playerSnake.getLength(), currentLength);
        assertEquals(aiSnake.getLength(), currentLength);
        assertEquals(fruit.getFruitPos(), fruitCoord);
    }

    @Test
    public void testHandlePowerUp() throws IOException {

        Coordinate nextCoord = new Coordinate(playerSnake.getHeadPoint().getX(), playerSnake.getHeadPoint().getY() - 1);

        PowerUp pu = new PowerUp(map, nextCoord);

        playerSnake.updateSnake();
        playerSnake.handlePowerUp(pu);

        if(playerSnake.hasMine()
                || playerSnake.hasFreeze()
                || playerSnake.hasControlsInverter()
                || playerSnake.hasWallSkipper()
                || playerSnake.hasSpeedUp())
        {
            assertNull(pu.getCoordinate());
        }
        else {

            assertNotEquals(pu.getCoordinate(), nextCoord);
        }
    }

    @Test
    public void testFreeze() {

        snake.setHasFreeze(true);
        snake.initFreeze();

        assertFalse(snake.hasFreeze());
        assertTrue(snake.freezeOtherPlayer());

        for(int i = 0; i < 9; i++) {

            snake.freezeOtherPlayer();
        }

        assertFalse(snake.freezeOtherPlayer());
    }

    @Test
    public void testWallSkip() {

        playerSnake.respawnSnake();
        playerSnake.setHasWallSkipper(true);
        playerSnake.initWallSkipper();

        assertFalse(playerSnake.hasWallSkipper());

        Coordinate nextCoord = new Coordinate(playerSnake.getHeadPoint().getX(), playerSnake.getHeadPoint().getY() - 1);
        map.setMapValue(nextCoord, BoxStatus.WALL);

        for(int i = 0; i < map.getX(); i++) {

            for(int j = 0; j < map.getY(); j++) {

                System.out.print(map.getMapValue(i, j) + " ");
            }
            System.out.println();
        }

        playerSnake.updateSnake();

        assertEquals(playerSnake.getStartPoint().getY(), playerSnake.getHeadPoint().getY() + 1);
        assertEquals(playerSnake.getStartPoint().getX(), playerSnake.getHeadPoint().getX());
    }

    @Test
    public void testControlsInverter() {

        snake.setHasControlsInverter(true);
        snake.initControlsInverter();

        assertFalse(snake.hasControlsInverter());
        assertTrue(snake.invertControls());
    }

    @Test
    public void testSetMine() {

        playerSnake.spawnSnake();
        playerSnake.initBodyCoordinates(playerSnake.getStartPoint());

        for(int i = 0; i < 5; i++) {

            playerSnake.updateSnake();
        }
        playerSnake.setHasMine(true);
        playerSnake.setMine();

        assertFalse(playerSnake.hasMine());
        assertTrue(map.getMapValue(playerSnake.getSnakeBodyCoordinates().get(4)).equals(BoxStatus.PLACED_MINE));
    }
}
