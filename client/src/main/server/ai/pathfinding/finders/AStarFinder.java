package server.ai.pathfinding.finders;

import server.ai.pathfinding.heuristics.AStarHeuristic;
import server.interfaces.FinderIF;

/**
 * An A Star path finding class, which uses A Star pathfinding with an A Star Heuristic to find the shortest path on a grid graph
 *
 * @author Daniel Batchford
 */
public class AStarFinder extends QueueFinder implements FinderIF {

    /**
     * @param xDim the X dimension of the input graph
     * @param yDim the Y dimension of the input graph
     * @author Daniel Batchford
     */
    public AStarFinder(int xDim, int yDim) {

        super(xDim, yDim);
        this.heuristic = new AStarHeuristic();
        this.useWrapped = false;
        this.invertSearch = false;
    }
}