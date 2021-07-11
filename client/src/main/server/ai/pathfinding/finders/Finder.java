package server.ai.pathfinding.finders;

import server.ai.pathfinding.NoPathFoundException;
import server.ai.pathfinding.Node;
import server.ai.pathfinding.NodeGrid;
import server.ai.pathfinding.PathFindingException;

import java.util.List;

/**
 * Pathfinding superclass
 *
 * @author Daniel Batchford
 */
public class Finder {

    protected Node start;
    protected Node end;

    protected Finder() {

    }

    /**
     * Finds a path in NodeGrid, from startCord to endCord
     *
     * @param startCord A 2 length int array representing the start coordinate
     * @param endCord   A 2 length int array representing the end coordinate
     * @param grid      A NodeGrid representing the grid graph
     * @return A list of int[] coordinates representing a path from startCord to endCord
     * @throws PathFindingException Thrown when invalid pathfinding parameters are used
     * @throws NoPathFoundException Thrown when no path is found
     * @author Daniel Batchford
     */
    public List<int[]> findPath(int[] startCord, int[] endCord, NodeGrid grid) throws PathFindingException, NoPathFoundException {

        if (startCord == null) throw new PathFindingException("Start co-ordinate was null");
        if (endCord == null) throw new PathFindingException("End co-ordinate was null");
        if (grid == null) throw new PathFindingException("Grid provided was null");

        if (startCord.length != 2) throw new PathFindingException("Start co-ordinate specified is not of size 2");
        if (endCord.length != 2) throw new PathFindingException("End co-ordinate specified is not of size 2");

        int[] dim = grid.getDimensions();

        if (startCord[0] < 0 || startCord[0] >= dim[0] || startCord[1] < 0 || startCord[1] >= dim[1]) {
            throw new PathFindingException("Start coordinate (" + startCord[0] + "," + startCord[1] + ") was outside the grid range.");
        }

        if (endCord[0] < 0 || endCord[0] >= dim[0] || endCord[1] < 0 || endCord[1] >= dim[1]) {
            throw new PathFindingException("End coordinate (" + endCord[0] + "," + endCord[1] + ") was outside the grid range.");
        }

        this.start = grid.getNodes()[startCord[0]][startCord[1]];
        this.end = grid.getNodes()[endCord[0]][endCord[1]];

        if (!start.isWalkable()) {
            throw new PathFindingException("Start Square " + start.toString() + " was not walkable.");
        }
        if (!end.isWalkable()) {
            throw new PathFindingException("End Square " + end.toString() + " was not walkable.");
        }

        this.start.setParent(null);

        return null;
    }
}
