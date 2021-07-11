package server.game.managers.snakemanager;

import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;

/**
 * A subclass of Snake which retrieves ai movements from the ai handler
 */
public class PlayerSnake extends Snake {

    /**
     * Constructor for the player snake
     *
     * @param speed the speed of the snake
     * @param map   the map to use
     */
    public PlayerSnake(int speed, Map map) {

        super(speed, map);
        super.playerBox = BoxStatus.PLAYER_A;
        super.initBodyCoordinates(startPoint);

    }

    /**
     * Constructor for the player snake
     *
     * @param speed the speed of the snake
     * @param map   the map to use
     * @param skin  the skin to use
     * @param lives tne number of lives to use
     */
    public PlayerSnake(int speed, Map map, SnakeSkin skin, int lives) {

        super(speed, map, skin, lives);
        super.playerBox = BoxStatus.PLAYER_A;
        super.initBodyCoordinates(startPoint);

    }
}
