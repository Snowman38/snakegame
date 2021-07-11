package server.ai.pathfinding;

/**
 * Exception class used when an invalid call to the pathfinding package is called.
 *
 * @author Daniel Batchford
 */
public class PathFindingException extends Exception {

    /**
     * @param message The error message
     * @author Daniel Batchford
     */
    public PathFindingException(String message) {

        super(message);
    }
}
