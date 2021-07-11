package server.ai.pathfinding.heuristics;

import server.ai.pathfinding.Node;

/**
 * An A Star Heuristic, using modified manhattan distance
 *
 * @author Daniel Batchford
 */
public class AStarHeuristic extends Heuristic {

    /**
     * This heuristic is modified to prefer straighter paths over jagged ones.
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

        if (start[0] - end[0] == 0) return Math.abs(start[1] - end[1]);
        if (start[1] - end[1] == 0) return Math.abs(start[0] - end[0]);
        return 200 * Math.min(Math.abs(start[0] - end[0]), Math.abs(start[1] - end[1]));
    }

}
