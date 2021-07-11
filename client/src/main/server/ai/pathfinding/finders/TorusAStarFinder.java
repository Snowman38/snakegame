package server.ai.pathfinding.finders;

import server.ai.pathfinding.heuristics.TorusAStarHeuristic;
import server.interfaces.FinderIF;

/**
 * An A Star path finding class, which uses A Star pathfinding with an A Star Heuristic to find the shortest path on a grid graph.
 * This version accounts for paths which wrap along edges in a torus geometry
 *
 * @author Daniel Batchford
 */
public class TorusAStarFinder extends QueueFinder implements FinderIF {

    /**
     * @param xDim the X dimension of the input graph
     * @param yDim the Y dimension of the input graph
     * @author Daniel Batchford
     */
    public TorusAStarFinder(int xDim, int yDim) {

        super(xDim, yDim);
        this.heuristic = new TorusAStarHeuristic(xDim, yDim);
        this.useWrapped = true;
        this.invertSearch = false;
    }
}