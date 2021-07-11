package server.game.managers.snakemanager;

/**
 * Exception class handling errors when loading a snake image
 */
public class SnakeImageLoadException extends Exception {

    /**
     * Thrown when reading a snake image fails
     *
     * @param message the thrown message
     */
    public SnakeImageLoadException(String message) {

        super(message);
    }
}

