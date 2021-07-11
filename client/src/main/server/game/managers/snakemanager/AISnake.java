package server.game.managers.snakemanager;

import logging.Logger;
import server.ai.AIHandler;
import server.ai.Difficulty;
import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.managers.powerupsmanager.PowerUp;
import server.game.usables.Coordinate;
import server.game.usables.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import handlers.AudioController;

/**
 * A subclass of Snake which retrieves ai movements from the ai handler
 */
public class AISnake extends Snake {

    private final AIHandler aiHandler;

    protected AISnake(int speed, Map map, Difficulty difficulty) {

        super(speed, map);
        super.playerBox = BoxStatus.PLAYER_B;
        this.aiHandler = new AIHandler(map.getX(), map.getY(), difficulty);
        super.initBodyCoordinates(startPoint);
    }

    /**
     * Constructs an AI snake
     *
     * @param speed      the speed of the snake to use
     * @param map        the selected map
     * @param skin       the selected snake skin
     * @param lives      the number of lives to use
     * @param difficulty the selected ai difficulty
     */
    public AISnake(int speed, Map map, SnakeSkin skin, int lives, Difficulty difficulty) {

        super(speed, map, skin, lives);
        super.playerBox = BoxStatus.PLAYER_B;
        this.aiHandler = new AIHandler(map.getX(), map.getY(), difficulty);
        super.initBodyCoordinates(startPoint);
    }

    /**
     * Updates a snake
     *
     * @param opposingPlayerSnake the enemy snake
     * @param targetLoc           the locations of fruit / powerups
     */
    @Override
    public void updateSnake(Snake opposingPlayerSnake, Coordinate targetLoc) {

        // Transform into data types needed by ai handler

        Coordinate[][] board = new Coordinate[this.map.getX()][this.map.getY()];

        for (int x = 0; x < this.map.getX(); x++) {
            for (int y = 0; y < this.map.getY(); y++) {
                BoxStatus status = this.map.getMapValue(x, y);
                boolean walkable = !status.equals(BoxStatus.WALL);
                board[x][y] = new Coordinate(x, y, walkable);
            }
        }
        List<Coordinate> snakeAI = super.snakeBodyCoordinates;
        List<Coordinate> snakeP = opposingPlayerSnake.getSnakeBodyCoordinates();

        List<Coordinate> fruits = new ArrayList<>();
        fruits.add(targetLoc);

        Direction nextAIDirection = aiHandler.getNextMove(board, snakeAI, snakeP, fruits);
        if (nextAIDirection == null) {
            Logger.error("Next AI direction was null");
        } else {

            if (opposingPlayerSnake.invertControls()) {
                super.setDirection(nextAIDirection.getOpposite());
            } else {
                super.setDirection(nextAIDirection);
            }
        }

        super.updateSnake();
    }
    
    /**
     * Used for the collision with a PowerUp object
     *
     * @param power the powerup to handle
     * @throws IOException if the audio controller fails
     */
    @Override
    public void handlePowerUp(PowerUp power) throws IOException {

    	power.despawnPowerUp2(map);
    }
}
