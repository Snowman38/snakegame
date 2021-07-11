package server.ai.pathfinding;

/**
 * Exception class used when no path is found on a NodeGrid
 *
 * @author Daniel Batchford
 */
public class NoPathFoundException extends Exception {

    /**
     * @param message The error message
     * @author Daniel Batchford
     */
    public NoPathFoundException(String message) {

        super(message);
    }
}
