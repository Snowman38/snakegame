package server.ai.pathfinding.finders;

import server.ai.pathfinding.NoPathFoundException;
import server.ai.pathfinding.Node;
import server.ai.pathfinding.NodeGrid;
import server.ai.pathfinding.PathFindingException;
import server.ai.pathfinding.heuristics.Heuristic;
import server.ai.util.Util;
import server.interfaces.FinderIF;

import java.util.*;

/**
 * A pathfinding superclass which uses a priority queue, open and closed sets to perform pathfinding with a given heuristic
 *
 * @author Daniel Batchford
 */
public class QueueFinder extends Finder implements FinderIF {

    protected final Set<Node> closedList;
    protected final PriorityQueue<Node> openList;

    protected final int xDim;
    protected final int yDim;

    protected Heuristic heuristic;
    protected boolean useWrapped;
    protected boolean invertSearch;

    protected QueueFinder(int xDim, int yDim) {

        super();
        this.xDim = xDim;
        this.yDim = yDim;

        this.closedList = new HashSet<>();

        Comparator<Node> comparator = (o1, o2) -> {
            if (o1.getF() == o2.getF()) return 0;
            return (o1.getF() > o2.getF()) ? 1 : -1;
        };

        this.openList = new PriorityQueue<>(comparator);
    }

    protected List<int[]> backTrace(Node node) {

        if (node == null) return null;

        List<int[]> route = new LinkedList<>();

        route.add(node.toIntArray());
        while (node.getParent() != null) {
            node = node.getParent();
            route.add(node.toIntArray());
        }
        Collections.reverse(route);
        return route;
    }

    protected boolean shouldConsiderNode(Node workingNode, Node neighbor) {

        if (!this.invertSearch) {
            return workingNode.getG() + Util.getDistance(workingNode, neighbor, xDim, yDim, this.useWrapped) < neighbor.getG();
        }
        return workingNode.getF() < neighbor.getF();
    }

    /**
     * Finds a path in NodeGrid, from startCord to endCord
     *
     * @param startCord A 2 length int array representing the start coordinate
     * @param endCord   A 2 length int array representing the end coordinate
     * @param grid      A NodeGrid representing the grid graph
     * @author Daniel Batchford
     */
    @Override
    public List<int[]> findPath(int[] startCord, int[] endCord, NodeGrid grid) throws NoPathFoundException, PathFindingException {

        super.findPath(startCord, endCord, grid);

        openList.clear();
        closedList.clear();

        start.setG(0);
        start.setF(0);
        openList.add(start);

        while (!(openList.isEmpty())) {

            Node workingNode = openList.poll();
            closedList.add(workingNode);

            if (workingNode.equals(end)) {
                return backTrace(workingNode);
            }

            for (Node n : grid.getNeighbors(workingNode, useWrapped)) {

                if (closedList.contains(n) || !n.isWalkable()) {
                    continue;
                }
                if (shouldConsiderNode(workingNode, n) || !openList.contains(n)) {

                    n.setG(workingNode.getG() + Util.getDistance(workingNode, n, xDim, yDim, useWrapped));
                    n.setF(n.getG() + this.heuristic.getHeuristic(n, end));

                    n.setParent(workingNode);

                    if (!openList.contains(n)) {
                        openList.add(n);
                    }
                }
            }
        }
        throw new NoPathFoundException("No path was found from " + start.toString() + " to (" + end.toString());
    }
}
