package server.ai.pathfinding.finders;

import server.ai.pathfinding.heuristics.LongPathHeuristic;
import server.interfaces.FinderIF;

/**
 * A longest path finding class, which uses a custom heuristic to find a sufficiently long path on a grid graph
 *
 * @author Daniel Batchford
 */
public class LongestPathFinder extends QueueFinder implements FinderIF {

    /**
     * @param xDim               the X dimension of the input graph
     * @param yDim               the Y dimension of the input graph
     * @param useWrappedDistance whether to allow wrapping around the edges of the grid graph
     * @author Daniel Batchford
     */
    public LongestPathFinder(int xDim, int yDim, boolean useWrappedDistance) {

        super(xDim, yDim);
        this.heuristic = new LongPathHeuristic();
        this.useWrapped = useWrappedDistance;
        this.invertSearch = true;
    }
}
