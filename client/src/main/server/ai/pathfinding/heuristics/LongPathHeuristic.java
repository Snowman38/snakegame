package server.ai.pathfinding.heuristics;

import server.ai.pathfinding.Node;
import server.ai.util.Util;

/**
 * A longest path heuristic class
 *
 * @author Daniel Batchford
 */
public class LongPathHeuristic extends Heuristic {

    /**
     * @author Daniel Batchford
     */
    public LongPathHeuristic() {

    }

    /**
     * This heuristic is negatively weighted such that it prefers longer paths over shorter ones
     *
     * @param na The starting node
     * @param nb The ending node
     * @return The heuristic value, as a float
     * @author Daniel Batchford
     */
    @Override
    public float getHeuristic(Node na, Node nb) {

        int[] start = na.toIntArray();
        int[] end = nb.toIntArray();
        return -100 * Util.getDistance(start, end);
    }

}
