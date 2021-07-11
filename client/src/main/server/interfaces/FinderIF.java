package server.interfaces;

import server.ai.pathfinding.NoPathFoundException;
import server.ai.pathfinding.NodeGrid;
import server.ai.pathfinding.PathFindingException;

import java.util.List;

public interface FinderIF {

    List<int[]> findPath(int[] startCord, int[] endCord, NodeGrid grid) throws PathFindingException, NoPathFoundException;
}