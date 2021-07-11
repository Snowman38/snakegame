package server.networking;

import handlers.AudioController;
import interfaces.GameConstants;
import interfaces.PathConstants;
import javafx.scene.canvas.GraphicsContext;
import logging.Logger;
import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.managers.powerupsmanager.*;
import server.game.managers.snakemanager.SnakeSkin;
import server.game.usables.Coordinate;
import server.game.usables.Direction;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Snake implements Serializable, PathConstants, GameConstants {

    private final int score;
    private final Coordinate startPoint;
    private final Map map;
    private final SnakeSkin skin;
    private final Mine mine;
    private final Freeze freeze;
    private final WallSkipper wallSkipper;
    private final ControlsInverter controlsInverter;
    private int length;
    private int speed;
    private int lives;
    private Direction direction;
    private CopyOnWriteArrayList<Coordinate> snakeBodyCoordinates = new CopyOnWriteArrayList<>();
    private boolean isDead;
    private boolean hasFreeze;
    private boolean hasMine;
    private boolean hasWallSkipper;
    private boolean hasControlsInverter;
    private boolean hasSpeedUp;
    private boolean hasCoin;

    /**
     * Constructor to create a snake object
     *
     * @param speed      Speed of the snake
     * @param lives      Initial lives of the snake
     * @param startPoint Initial position of snake
     * @param map        The map the snake will be drawn onto
     * @param skin       The skin the snake has equipped
     */

    public Snake(int speed, int lives, Coordinate startPoint, Map map, SnakeSkin skin) {

        this.length = SNAKE_INIT_LENGTH;
        this.speed = speed;
        this.score = 0;
        this.lives = lives;
        this.direction = Direction.UP;
        this.startPoint = startPoint;
        this.map = map;
        this.assignBodyCoordinates(startPoint);
        this.skin = skin;
        this.isDead = false;
        this.mine = new Mine(map);
        this.freeze = new Freeze(map);
        this.wallSkipper = new WallSkipper(map);
        this.controlsInverter = new ControlsInverter(map);
        this.hasFreeze = false;
        this.hasMine = false;
        this.hasWallSkipper = false;
        this.hasControlsInverter = false;
        this.hasCoin = false;
    }

    private void moveSnake() {

        for (int i = 0; i < this.speed; i++) {
            Coordinate p = new Coordinate(this.snakeBodyCoordinates.get(0).getX(), this.snakeBodyCoordinates.get(0).getY());

            switch (this.direction) {
                case LEFT:
                    if (p.getX() - 1 < 0) {
                        p.setX(map.getX() - 1);
                        break;
                    }
                    p.addX(-1);
                    break;
                case RIGHT:
                    if (p.getX() + 1 > map.getX() - 1) {
                        p.setX(0);
                        break;
                    }
                    p.addX(1);
                    break;

                case UP:
                    if (p.getY() - 1 < 0) {

                        p.setY(map.getY() - 1);
                        break;
                    }
                    p.addY(-1);
                    break;

                case DOWN:
                    if (p.getY() + 1 > map.getY() - 1) {

                        p.setY(0);
                        break;
                    }
                    p.addY(1);
                    break;

                default:
                    break;
            }

            this.replaceEachCoordinate(p);
        }
    }

    private void replaceEachCoordinate(Coordinate newCoord) {

        for (int i = snakeBodyCoordinates.size() - 1; i >= 1; i--) {
            snakeBodyCoordinates.get(i).setX(snakeBodyCoordinates.get(i - 1).getX());
            snakeBodyCoordinates.get(i).setY(snakeBodyCoordinates.get(i - 1).getY());
        }
        snakeBodyCoordinates.set(0, newCoord);

    }

    private void assignBodyCoordinates(Coordinate newCoord) {

        this.snakeBodyCoordinates.clear();
        this.snakeBodyCoordinates.add(newCoord);

        for (int i = 1; i < this.length; i++) {

            snakeBodyCoordinates.add(new Coordinate(newCoord.getX(), newCoord.getY()

                    /*+i remove comment to start with full sized snake.*/));
        }
    }

    private boolean hasPowerUp() {

        return hasFreeze || hasMine || hasWallSkipper || hasControlsInverter || hasSpeedUp;
    }

    private void getRandomPowerUp(MultiplayerPowerUp power) throws IOException {

        if (power.getPowerType() == BoxStatus.MINE) {

            hasMine = true;
            Logger.debug("Received mine");
        } else if (power.getPowerType() == BoxStatus.FREEZE) {

            hasFreeze = true;
            Logger.debug("Received freeze");
        } else if (power.getPowerType() == BoxStatus.SKIP) {

            hasWallSkipper = true;
            Logger.debug("Received WallSkip");
        } else if (power.getPowerType() == BoxStatus.SPEED) {

            hasSpeedUp = true;
            Logger.debug("Received SpeedUp");
        } else if (power.getPowerType() == BoxStatus.REVERSE) {

            hasControlsInverter = true;
            Logger.debug("Received Controls");
        } else if (power.getPowerType() == BoxStatus.COIN) {
            hasCoin = true;
        }
    }

    /**
     * Method to spawn a snake at a valid random coordinate
     *
     * @return The coordinate of the spawn point
     */

    public Coordinate spawnSnake() {

        Random rand = new Random();
        Coordinate spawn;
        int x;
        int y;

        while (true) {

            x = rand.nextInt(map.getX() - 8);
            y = rand.nextInt(map.getY() - 8);
            spawn = new Coordinate(x + 3, y + 3);

            if (getMap().getMapValue(spawn) == BoxStatus.EMPTY &&
                    getMap().getMapValue(new Coordinate(spawn.getX(), spawn.getY() - 1)) == BoxStatus.EMPTY &&
                    getMap().getMapValue(new Coordinate(spawn.getX(), spawn.getY() - 2)) == BoxStatus.EMPTY) {

                return spawn;
            }
        }
    }

    public int getLength() {

        return this.length;
    }

    public void setLength(int length) {

        this.length = length;
    }

    public int getScore() {

        return score;
    }

    public Map getMap() {

        return this.map;
    }

    public void setSpeed(int speed) {

        this.speed = speed;
    }

    public CopyOnWriteArrayList<Coordinate> getSnakeBodyCoordinates() {

        return snakeBodyCoordinates;
    }

    public void setSnakeBodyCoordinates(CopyOnWriteArrayList<Coordinate> coords) {

        this.snakeBodyCoordinates = coords;
    }

    public Direction getDirection() {

        return this.direction;
    }

    public void setDirection(Direction direction) {

        this.direction = direction;
    }

    public Coordinate getHeadPoint() {

        return this.snakeBodyCoordinates.get(0);
    }

    //Helper method for moveSnake() that replaces each Coordinate of the snake body to the one in front of it.

    public void setHeadPoint(Coordinate coord) {

        this.snakeBodyCoordinates.set(0, coord);
    }

	   /* initializes the snake's body vertically. So if the snake head is at (50,50)
    and the snake length=5 the head will be at 50,50. the rest of the body will be placed on the y axis up to 50,55 */

    public int getLives() {

        return this.lives;
    }

    public void setLives(int lives) {

        this.lives = lives;
    }

    /**
     * Method to check if a snake is dead
     *
     * @return True if the snake is dead and false otherwise
     */
    public boolean isDead() {

        return this.isDead;
    }

    /**
     * Method to draw the snake onto the users screen
     *
     * @param gc         The graphics context of the canvas the snake should be drawn on
     * @param squareSize the size of each pixel on the map
     */

    public void drawSnake(GraphicsContext gc, int squareSize) {

        gc.drawImage(this.skin.getHeadImage(), this.getHeadPoint().getX() * squareSize, this.getHeadPoint().getY() * squareSize, squareSize, squareSize);

        for (int i = 1; i < this.snakeBodyCoordinates.size(); i++) {

            gc.drawImage(this.skin.getBodyImage(), this.snakeBodyCoordinates.get(i).getX() * squareSize,
                    this.snakeBodyCoordinates.get(i).getY() * squareSize, squareSize, squareSize);
        }

        this.moveSnake();
    }

    /**
     * Method to increase the snakes size by an integer amount
     *
     * @param length The length to increase the snakes size by
     */

    public void grow(int length) {

        Coordinate last = this.snakeBodyCoordinates.get(this.snakeBodyCoordinates.size() - 1);
        for (int i = 1; i <= length; i++) {
            Coordinate p = new Coordinate(last.getX(), last.getY());
            this.snakeBodyCoordinates.add(p);
        }
        this.length = this.length + length;
    }



    /* Checks if the Snake has a PowerUp */

    /**
     * Method to decrease the snakes size by an integer amount
     *
     * @param length The length to decrease the snakes size by
     */

    public void shrink(int length) {

        if (snakeBodyCoordinates.size() == 1) {
            AudioController.deathSound();
            setLives(getLives() - 1);
            this.length = 5;
            this.respawnSnake();
        } else {
            for (int i = 0; i < length; i++) {
                this.snakeBodyCoordinates.remove(snakeBodyCoordinates.size() - 1);
            }
            this.length = this.length - length;
        }
    }

    /**
     * Method to respawn the snake at its original start point
     */

    public void respawnSnake() {

        if (lives <= 0 && !isDead) {
            isDead = true;
        }

        if (lives > 0 && isDead) {
            isDead = false;
        }

        if (!isDead) {
            this.direction = Direction.UP;
            assignBodyCoordinates(startPoint);

        } else {
            Logger.debug("Snake is out of lives");
        }
    }

	    /* Generates a random PowerUp, based on the preset chances of getting one
	        Mine - 18%
	        Freeze - 18%
	        WallSkip - 18%
	        SpeedUp - 18%
	        Inverting Other Player's Controls - 8%
	        Money Prize - 2%
	     */

    /**
     * Method to handle a collision between a snake and a powerup
     *
     * @param power the powerup the snake has collided with
     * @return Whether or no the snake has captured the powerup
     * @throws IOException if the AudioController fails
     */

    public boolean handlePowerUp(MultiplayerPowerUp power) throws IOException {

        if (snakeBodyCoordinates.get(0).equals(power.getCoordinate()) && power.getPowerType().equals(BoxStatus.COIN)) {
            AudioController.collectCoinSound();
            getRandomPowerUp(power);
            return true;
        } else if (snakeBodyCoordinates.get(0).equals(power.getCoordinate()) && !hasPowerUp()) {
            AudioController.collectSound();
            getRandomPowerUp(power);
            return true;
        } 
        return false;
    }

    /**
     * Method to initialise the timer for the wall skip powerup
     */

    public void initWallSkipper() {

        wallSkipper.setSkipTimer(5);
        hasWallSkipper = false;
    }

    /**
     * Method to check whether the timer for the wall skip is still active
     *
     * @return Whether the timer is greater than 0 or not
     */

    public boolean skipWall() {

        if (wallSkipper.getSkipTimer() > 0) {

            wallSkipper.setSkipTimer(wallSkipper.getSkipTimer() - 1);
            return true;
        } else {

            return false;
        }
    }

    /**
     * Method to initialise the timer for the control inverter powerup
     */

    public void initControlsInverter() {

        controlsInverter.setInvertTimer(15);
        hasControlsInverter = false;
    }

    /**
     * Method to check whether the timer for the controls inverter is still active
     *
     * @return Whether the timer is greater than 0 or not
     */

    public boolean invertControls() {

        if (controlsInverter.getInvertTimer() > 0) {

            controlsInverter.setInvertTimer(controlsInverter.getInvertTimer() - 1);
            return true;
        } else {

            return false;
        }
    }

    /**
     * Method to plant a mine at the snakes last body coordinate
     */

    public void setMine() {

        if (hasMine) {
            mine.plantMine(snakeBodyCoordinates.get(snakeBodyCoordinates.size() - 1));
            hasMine = false;
        }
    }

    /**
     * Method to initialise the timer for the freeze powerup
     */

    public void initFreeze() {

        freeze.setFreezeTimer(10);
        hasFreeze = false;
    }

    /**
     * Method to check whether the timer for the freeze powerup is still active
     *
     * @return Whether the timer is greater than 0 or not
     */
    public boolean freezeOtherPlayer() {

        if (freeze.getFreezeTimer() > 0) {

            freeze.setFreezeTimer(freeze.getFreezeTimer() - 1);
            return true;
        } else {

            return false;
        }
    }

    public boolean hasMine() {

        return hasMine;
    }

    public boolean hasFreeze() {

        return hasFreeze;
    }

    public boolean hasWallSkipper() {

        return hasWallSkipper;
    }

    public boolean hasControlsInverter() {

        return hasControlsInverter;
    }

    public boolean hasSpeedUp() {

        return hasSpeedUp;
    }

    public boolean hasCoin() {

        return hasCoin;
    }

    public void setCoin(Boolean bool) {

        hasCoin = bool;
    }

    public void setInvert(Boolean bool) {

        hasControlsInverter = bool;
    }

    public void setFreeze(Boolean bool) {

        hasFreeze = bool;
    }

    public void setMine(Boolean bool) {

        hasMine = bool;
    }

    public void setSkip(Boolean bool) {

        hasWallSkipper = bool;
    }

    /**
     * Method to double the snakes speed for 500ms
     */

    public void speedUp() {

        int durationInMiliSeconds = 500;

        hasSpeedUp = false;
        speed = 2;

        ScheduledExecutorService scheduler
                = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> speed = 1;

        scheduler.schedule(task, durationInMiliSeconds, TimeUnit.MILLISECONDS);
        scheduler.shutdown();
    }

}
