package server.game.managers.mapmanager;

/**
 * Exception class handling errors when loading a map image
 */
public class MapImageLoadException extends Exception {

    /**
     * Thrown when reading a map image fails
     *
     * @param message the thrown message
     */
    public MapImageLoadException(String message) {

        super(message);
    }
}
