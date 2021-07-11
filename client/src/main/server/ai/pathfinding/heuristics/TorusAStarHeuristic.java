package server.ai.pathfinding.heuristics;

import server.ai.pathfinding.Node;

/**
 * An A Star Heuristic, using modified manhattan distance. This heuristic also considers wrapped distance
 *
 * @author Daniel Batchford
 */
public class TorusAStarHeuristic extends Heuristic {

    private final int xDim;
    private final int yDim;

    /**
     * @param xDim The x dimension of the grid graph
     * @param yDim The y dimension of the grid graph
     * @author Daniel Batchford
     */
    public TorusAStarHeuristic(int xDim, int yDim) {

        this.xDim = xDim;
        this.yDim = yDim;
    }

    /**
     * This heuristic uses a modified version of A*. It considers the minimum of the wrapped distance and the actual distance as the heuristic,
     * in manhattan form.
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

        int dx = Math.abs(start[0] - end[0]);
        int dy = Math.abs(start[1] - end[1]);

        return Math.min(dx, xDim - dx) + Math.min(dy, yDim - dy);
    }
}
