package server.ai.pathfinding.heuristics;

import logging.Logger;
import server.ai.pathfinding.Node;

/**
 * Heuristic Super Class
 *
 * @author Daniel Batchford
 */
public abstract class Heuristic {

    /**
     * @param s The starting node
     * @param e The ending node
     * @return The heuristic value, as a float
     * @author Daniel Batchford
     */
    public float getHeuristic(Node s, Node e) {

        Logger.error("Should not be called");
        return 0.0f;
    }
}