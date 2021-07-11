package server.game.managers.snakemanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import handlers.AudioController;
import handlers.FileHandler;
import handlers.SinglePlayer;
import interfaces.GameConstants;
import interfaces.PathConstants;
import javafx.scene.canvas.GraphicsContext;
import logging.Logger;
import server.game.Fruit;
import server.game.managers.mapmanager.BoxStatus;
import server.game.managers.mapmanager.Map;
import server.game.managers.powerupsmanager.ControlsInverter;
import server.game.managers.powerupsmanager.Freeze;
import server.game.managers.powerupsmanager.Mine;
import server.game.managers.powerupsmanager.PowerUp;
import server.game.managers.powerupsmanager.WallSkipper;
import server.game.usables.Coordinate;
import server.game.usables.Direction;

/**
 * A superclass controlling snake behaviour
 */
public class Snake implements PathConstants, GameConstants {

    public static int valueOfFruit;
    protected final LinkedList<Coordinate> snakeBodyCoordinates = new LinkedList<>();
    private final int score;
    private final Mine mine;
    private final Freeze freeze;
    private final WallSkipper wallSkipper;
    private final ControlsInverter controlsInverter;
    private final ArrayList<Coordinate> skippedWalls;
    protected Map map;
    protected Coordinate startPoint;
    protected Direction direction;
    protected BoxStatus playerBox;
    private int speed;
    private int lives;
    private SnakeSkin skin;
    private boolean hasFreeze;
    private boolean hasMine;
    private boolean hasWallSkipper;
    private boolean hasControlsInverter;
    private boolean hasSpeedUp;
    private Coordinate notCaughtPower;
    private BoxStatus notCaughtPowerStatus;

    private boolean isDead;
    private int currentLength;

    /* Constructors */
    protected Snake(int speed, Map map) {

        this.speed = speed;
        this.score = 0;
        this.lives = NO_OF_LIVES;
        this.direction = Direction.UP;
        this.map = map;
        this.startPoint = spawnSnake();
        this.initBodyCoordinates(startPoint);
        this.isDead = false;
        this.currentLength = SNAKE_INIT_LENGTH;
        this.mine = new Mine(map);
        this.freeze = new Freeze(map);
        this.wallSkipper = new WallSkipper(map);
        this.controlsInverter = new ControlsInverter(map);
        this.hasFreeze = false;
        this.hasMine = false;
        this.hasWallSkipper = false;
        this.hasControlsInverter = false;
        this.skippedWalls = new ArrayList<>();
        this.notCaughtPower = null;
        this.notCaughtPowerStatus = BoxStatus.EMPTY;

        try {
            this.skin = new SnakeSkin(SNAKE_HEAD_URL, BODY_SKIN_URL);
        } catch (SnakeImageLoadException e) {
            Logger.error("Missing default snake skin files.");
        }
    }

    protected Snake(int speed, Map map, SnakeSkin skin, int lives) {

        this.speed = speed;
        this.score = 0;
        this.lives = lives;
        this.direction = Direction.UP;
        this.map = map;
        this.startPoint = spawnSnake();
        this.skin = skin;
        this.isDead = false;
        this.currentLength = SNAKE_INIT_LENGTH;
        this.mine = new Mine(map);
        this.freeze = new Freeze(map);
        this.wallSkipper = new WallSkipper(map);
        this.controlsInverter = new ControlsInverter(map);
        this.hasFreeze = false;
        this.hasMine = false;
        this.hasWallSkipper = false;
        this.hasControlsInverter = false;
        this.hasSpeedUp = false;
        this.skippedWalls = new ArrayList<>();
        this.notCaughtPower = null;
    }

    /* initializes the snake's body vertically. So if the snake head is at (50,50)
    and the snake length=5 the head will be at 50,50. the rest of the body will be placed on the y axis up to 50,54. Uncomment offset for full body spawning */
    protected void initBodyCoordinates(Coordinate startCord) {

        this.snakeBodyCoordinates.clear();
        this.snakeBodyCoordinates.add(startCord);

        Coordinate currCord;

        for (int offset = 1; offset < currentLength; offset++) {
            currCord = new Coordinate(startCord.getX() /* + offset */, startCord.getY());
            map.setMapValue(currCord, this.playerBox);
            snakeBodyCoordinates.addLast(currCord);
        }
    }

    /* Helper method for moveSnake() that replaces each Coordinate of the snake body to the one in front of it. */
    private void travelForwards(Coordinate newCoord) {

        /* Here the collision checkers are being used, in case of a collision the snake respawns at
            startPoint
           If the WallSkipper PowerUp is being used, then the snake can walk through a set number of walls*/

        if (checkForWallCollision(newCoord) && skipWall()) {
            skippedWalls.add(newCoord);
            stepForward(newCoord);
        } else if (checkForWallCollision(newCoord) || checkForSnakeCollision(newCoord) || checkForMineCollision(newCoord)) {
            respawnSnake();
            this.shrink();
            this.lives--;
        } else {
            stepForward(newCoord);
        }
    }

    /* Method used to move the snake one tile */
    private void stepForward(Coordinate newCoord) {

        Coordinate lastCoord;
        if (!snakeBodyCoordinates.isEmpty()) {
            lastCoord = snakeBodyCoordinates.getLast();

            if (skippedWalls.contains(lastCoord)) {
                map.setMapValue(lastCoord, BoxStatus.WALL);
                skippedWalls.remove(0);
            } else if (lastCoord.equals(notCaughtPower)) {
                //replace mine with the other available
                map.setMapValue(lastCoord, this.notCaughtPowerStatus);
                notCaughtPower = null;
                notCaughtPowerStatus = BoxStatus.EMPTY;
            } else if (map.getMapValue(lastCoord).equals(BoxStatus.PLACED_MINE)) {

                Logger.debug("Placed mine");
            } else {
                map.setMapValue(lastCoord, BoxStatus.EMPTY);
            }

            map.setMapValue(newCoord, this.playerBox);

            this.snakeBodyCoordinates.removeLast();
            this.snakeBodyCoordinates.addFirst(newCoord);

        } else {
            lastCoord = startPoint;

            map.setMapValue(lastCoord, BoxStatus.EMPTY);
            map.setMapValue(newCoord, this.playerBox);

        }
    }

    /* Method used for increasing the length of the snake */
    private void grow() {

        Coordinate last = this.snakeBodyCoordinates.getLast();
        this.snakeBodyCoordinates.addLast(last);
        this.currentLength++;
        AudioController.consumeSound();
    }

    /* Method used for reducing the snake's length */
    private void shrink() {

        if (snakeBodyCoordinates.size() == 1) {
            this.lives--;
            this.resetLength();
            this.respawnSnake();
            AudioController.deathSound();
        } else {
            this.snakeBodyCoordinates.removeLast();
            this.currentLength--;
        }

    }

    /* Checks for collision for snakes and walls (depending on the input)
       If true, the snake respawns */
    private boolean checkForSnakeCollision(Coordinate posInFront) {

        /* Checks if the snake hits itself (other snake to be added) */

        BoxStatus snakeHeadStatus = map.getMapValue(posInFront);

        boolean collideStatus = snakeHeadStatus.equals(BoxStatus.PLAYER_A) || snakeHeadStatus.equals(BoxStatus.PLAYER_B);

        if (collideStatus) {
            Logger.debug("Snake Collision Occurred");
        }
        return collideStatus;
    }

    /* Checks if there is a wall */
    private boolean checkForWallCollision(Coordinate posInFront) {

        try {
            boolean collideStatus = map.getMapValue(posInFront).equals(BoxStatus.WALL);
            if (collideStatus) {
                Logger.debug("Wall Collision Occurred");
            }
            return collideStatus;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean hasPowerUp() {

        return hasFreeze || hasMine || hasWallSkipper || hasControlsInverter || hasSpeedUp;
    }

    /**
     * Generates a random PowerUp, based on the preset chances of getting one
     * Mine - 18%
     * Freeze - 18%
     * WallSkip - 18%
     * SpeedUp - 18%
     * Inverting Other Player's Controls - 8%
     * Money Prize - 2%
     */
    private void getRandomPowerUp(PowerUp power) throws IOException {

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
            Random rand = new Random();
            int balancePrize = rand.nextInt(500);
            FileHandler.updateBalance(balancePrize);
            Logger.debug("Received prize " + balancePrize);
            SinglePlayer.balance.setText("Balance: " + FileHandler.getBalance());
            power.spawnPowerUp(map);
        }
    }

    /* Checks for collision with mine */
    private boolean checkForMineCollision(Coordinate posInFront) {

        boolean collideStatus = map.getMapValue(posInFront).equals(BoxStatus.PLACED_MINE);
        if (collideStatus) {
            Logger.debug("Mine Collision Occurred");
            map.setMapValue(posInFront, BoxStatus.EMPTY);
        }
        return collideStatus;

    }

    /* Once the player initialises the WallSkipper, this method returns true as long as the timer is > 0
       This means that the player can skip through 5 walls (in this scenario) */
    private boolean skipWall() {

        if (wallSkipper.getSkipTimer() > 0 && !hasWallSkipper) {

            wallSkipper.setSkipTimer(wallSkipper.getSkipTimer() - 1);
            return true;
        } else {

            return false;
        }
    }

    private void resetLength() {

        this.currentLength = SNAKE_INIT_LENGTH;
    }

    /**
     * Spawns a snake in a random location on the map
     *
     * @return the head of the selected spawn
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

    /**
     * Respawns a snake
     */
    public void changeSpawn() {

        this.startPoint = spawnSnake();
    }

    /* Checks if the Snake has a PowerUp */

    // Empty method needed here for aiSnake override
    public void updateSnake(Snake opposingPlayerSnake, Coordinate fruitLoc) {

    }

    /**
     * Updates / moves the snake based on the direction that it has at the time of the movement
     * It also wraps such that moving off the left of the screen takes you to the right of the screen
     */
    public void updateSnake() {

        for (int i = 0; i < this.speed; i++) {

            Coordinate snakeHead = this.snakeBodyCoordinates.getFirst();

            int dirX = 0;
            int dirY = 0;

            switch (this.direction) {
                case LEFT -> dirX = -1;
                case RIGHT -> dirX = 1;
                case UP -> dirY = -1;
                case DOWN -> dirY = 1;
            }

            int newX = Math.floorMod(snakeHead.getX() + dirX, map.getX());
            int newY = Math.floorMod(snakeHead.getY() + dirY, map.getY());

            this.travelForwards(new Coordinate(newX, newY));
        }
    }

    /**
     * Renders a snake on the GraphicsContext
     *
     * @param gc         the GraphicsContext to use
     * @param squareSize the pixel width of each square on the map
     */
    public void renderSnake(GraphicsContext gc, int squareSize) {

        if (!isDead) {

            // Draw the head
            gc.drawImage(this.skin.getHeadImage(), this.getHeadPoint().getX() * squareSize, this.getHeadPoint().getY() * squareSize, squareSize, squareSize);

            // Draw rest of the body
            snakeBodyCoordinates.stream().skip(1).forEach((bodyCord) -> {
                gc.drawImage(this.skin.getBodyImage(), bodyCord.getX() * squareSize,
                        bodyCord.getY() * squareSize, squareSize, squareSize);
            });
        }
    }

    /**
     * Checks if a snake has collected a fruit and deals with it accordingly
     *
     * @param fruit      the Fruit object
     * @param otherSnake the enemy snake
     * @throws IOException if the data file fails to read
     */
    public void handleFruit(Fruit fruit, Snake otherSnake) throws IOException {

        if (snakeBodyCoordinates.getFirst().equals(fruit.getFruitPos())) {

            /* Grows the snake and respawns the fruit */
            grow();
            fruit.despawnFruit(map);
            fruit.spawnRandomFruit(map);

            //increase in balance after player eats an apple

            if (this.playerBox.equals(BoxStatus.PLAYER_A)) {
            	
                FileHandler.updateBalance(valueOfFruit);
                SinglePlayer.balance.setText("Balance: " + FileHandler.getBalance());
            }

            map.setMapValue(otherSnake.snakeBodyCoordinates.getLast(), BoxStatus.EMPTY);
            otherSnake.shrink();

        }
    }

    /**
     * Used to respawn the snake in the case of a collision
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

        	/* Checks if the snake didn't left any leftover Walls or PowerUps when the respawn occurs
        	    or adds an empty space otherwise */
            snakeBodyCoordinates.forEach((bodyCord) -> {

                if (skippedWalls.contains(bodyCord)) {

                    map.setMapValue(bodyCord, BoxStatus.WALL);
                    skippedWalls.remove(bodyCord);
                } else if (notCaughtPower != null && notCaughtPower.equals(bodyCord)) {

                    map.setMapValue(bodyCord, BoxStatus.SKIP);
                    notCaughtPower = null;
                } else {
                    map.setMapValue(bodyCord, BoxStatus.EMPTY);
                }
            });
            initBodyCoordinates(startPoint);
            AudioController.hitWallSound();

        } else {
            Logger.debug("Snake is out of lives");
        }

    }

    /**
     * Used for the collision with a PowerUp object
     *
     * @param power the powerup to handle
     * @throws IOException if the audio controller fails
     */
    public void handlePowerUp(PowerUp power) throws IOException {

        if (snakeBodyCoordinates.getFirst().equals(power.getCoordinate()) && power.getPowerType().equals(BoxStatus.COIN)) {
            power.despawnPowerUp(map);
            getRandomPowerUp(power);
            AudioController.collectCoinSound();
        } else if (snakeBodyCoordinates.getFirst().equals(power.getCoordinate()) && !hasPowerUp()) {

            power.despawnPowerUp(map);
            getRandomPowerUp(power);
            AudioController.collectSound();
        } else if (snakeBodyCoordinates.getFirst().equals(power.getCoordinate())) {

            notCaughtPower = power.getCoordinate();
            notCaughtPowerStatus = power.getPowerType();
        }
    }

    /**
     * Initialises the Freeze PowerUp once the Space button is pressed
     *
     * @return whether the player has a freeze powerup
     */
    public boolean initFreeze() {

        if (hasFreeze) {

            freeze.setFreezeTimer(10);
            hasFreeze = false;
            return true;
        } else {

            Logger.debug("The player doesn't have a Freeze PowerUp.");
            return false;
        }
    }

    /**
     * Freezes another player While this is true, the other player is frozen
     *
     * @return whether the enemy player is frozen
     */
    public boolean freezeOtherPlayer() {

        if (freeze.getFreezeTimer() > 0 && !hasFreeze) {

            freeze.setFreezeTimer(freeze.getFreezeTimer() - 1);
            return true;
        } else {

            return false;
        }
    }

    /**
     * Initialises the WallSkip PowerUp once the Space button is pressed
     *
     * @return whether the player has a wallskip powerup
     */
    public boolean initWallSkipper() {

        if (hasWallSkipper) {

            wallSkipper.setSkipTimer(5);
            hasWallSkipper = false;
            return true;
        } else {

            Logger.debug("The player doesn't have a Wall skip PowerUp.");
            return false;
        }
    }

    /**
     * Initialises the Controls Inverter PowerUp once the Space button is pressed
     *
     * @return whether the player has a controls inverter
     */
    public boolean initControlsInverter() {

        if (hasControlsInverter) {

            controlsInverter.setInvertTimer(10);
            hasControlsInverter = false;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Once the ControlsInverter has been initialised, this method returns true while the timer is greater than 0
     *
     * @return whether the invert controls powerup is active
     */

    public boolean invertControls() {

        if (controlsInverter.getInvertTimer() > 0 && !hasControlsInverter) {

            controlsInverter.setInvertTimer(controlsInverter.getInvertTimer() - 1);
            return true;
        } else {

            return false;
        }
    }

    /**
     * Used to plant a mine once the Space button is pressed
     */
    public void setMine() {

        if (hasMine) {
            mine.plantMine(snakeBodyCoordinates.getLast());
            hasMine = false;
        }
    }


    /* Getters and setters */

    public int getScore() {

        return this.score;
    }

    public int getLives() {

        return this.lives;
    }

    public void setLives(int lives) {

        this.lives = lives;
    }

    public boolean isDead() {

        return this.isDead;
    }

    public List<Coordinate> getSnakeBodyCoordinates() {

        return this.snakeBodyCoordinates;
    }

    public Direction getDirection() {

        return this.direction;
    }

    public void setDirection(Direction direction) {

        this.direction = direction;
    }

    /**
     * Used in SinglePlayer.java for input validation
     *
     * @param newDirection the requested direction
     * @return whether newDirection is opposite to the current snake's direction
     */
    public boolean isOppositeDirection(Direction newDirection) {

        return newDirection.getOpposite().equals(this.direction);
    }

    public Coordinate getHeadPoint() {

        return this.snakeBodyCoordinates.getFirst();
    }

    public Map getMap() {

        return this.map;
    }

    public void setMap(Map map) {

        this.map = map;
    }

    public boolean hasMine() {

        return this.hasMine;
    }

    public void setHasMine(boolean setHasMine) {

        this.hasMine = setHasMine;
    }

    public boolean hasFreeze() {

        return this.hasFreeze;
    }

    public void setHasFreeze(boolean setHasFreeze) {

        this.hasFreeze = setHasFreeze;
    }

    public boolean hasWallSkipper() {

        return this.hasWallSkipper;
    }

    public void setHasWallSkipper(boolean setHasWallSkipper) {

        this.hasWallSkipper = setHasWallSkipper;
    }

    public boolean hasControlsInverter() {

        return this.hasControlsInverter;
    }

    public void setHasControlsInverter(boolean setHasControlsInverter) {

        this.hasControlsInverter = setHasControlsInverter;
    }

    public boolean hasSpeedUp() {

        return this.hasSpeedUp;
    }

    public void setHasSpeedUp(boolean setHasSpeedUp) {

        this.hasSpeedUp = setHasSpeedUp;
    }

    /**
     * speeds up the snake for fixed duration.
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

    public Coordinate getStartPoint() {
        return this.startPoint;
    }

    public int getLength() {
        return this.currentLength;
    }
}
