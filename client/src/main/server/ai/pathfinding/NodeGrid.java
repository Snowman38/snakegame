package server.ai.pathfinding;

import logging.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * A node representation of a 2d grid. A NodeGrid contains a 2d array of nodes as well as a 2 length array of the dimensions
 * of the grid ( [xLength, yLength]
 *
 * @author Daniel Batchford
 */
public class NodeGrid {

    private final int[] dim;
    private final Node[][] nodes;

    /**
     * @param dimensions the dimensions of the 2d grid in form [xLength, yLength]
     * @throws PathFindingException Thrown if dimensions are invalid or null
     * @author Daniel Batchford
     */
    public NodeGrid(int[] dimensions) throws PathFindingException {

        if (dimensions == null) {
            throw new PathFindingException("Grid dimensions provided were null");
        }
        if (dimensions.length != 2) {
            throw new PathFindingException("Dimensions provided were not of length 2");
        }
        if (dimensions[0] <= 0) {
            throw new PathFindingException("X dimension was <= 0, cannot create grid");
        }
        if (dimensions[1] <= 0) {
            throw new PathFindingException("Y dimension was <= 0, cannot create grid");
        }

        this.nodes = new Node[dimensions[0]][dimensions[1]];

        for (int x = 0; x < dimensions[0]; x++) {
            for (int y = 0; y < dimensions[1]; y++) {
                nodes[x][y] = new Node(new int[]{x, y});
            }
        }

        this.dim = dimensions;

    }

    /**
     * @param cord     The 2 length int array representing a coordinate, in form [x,y]
     * @param walkable Whether this node should be walkable
     * @author Daniel Batchford
     */
    public void setWalkable(int[] cord, boolean walkable) {

        if (cord.length != 2) {
            Logger.error("Co-ordinate specified is not of size 2");
            return;
        }
        if (cord[0] < 0 || cord[0] >= dim[0] || cord[1] < 0 || cord[1] >= dim[1]) {
            Logger.error("Co-ordinate (" + cord[0] + "," + cord[1] + ") was outside the grid range.");
            return;
        }
        nodes[cord[0]][cord[1]].setWalkable(walkable);

    }

    public int[] getDimensions() {

        return this.dim;
    }

    /**
     * Returns the neighbors of a Node in a NodeGrid. If useWrapped is used, edge squares return squares on the opposing side.
     *
     * @param node       The node to fetch neighbors for
     * @param useWrapped Whether to consider edge wrapping
     * @return A list of the manhattan neighbors of the input node.
     * @author Daniel Batchford
     */
    public List<Node> getNeighbors(Node node, boolean useWrapped) {

        List<Node> neighbors = new ArrayList<>();

        int[] boxCord = node.toIntArray();

        if (useWrapped) {
            if (boxCord[0] > 0) {
                neighbors.add(this.nodes[boxCord[0] - 1][boxCord[1]]);
            } else {
                neighbors.add(this.nodes[dim[0] - 1][boxCord[1]]);
            }
            if (boxCord[0] < dim[0] - 1) {
                neighbors.add(this.nodes[boxCord[0] + 1][boxCord[1]]);
            } else {
                neighbors.add(this.nodes[0][boxCord[1]]);
            }
            if (boxCord[1] > 0) {
                neighbors.add(this.nodes[boxCord[0]][boxCord[1] - 1]);
            } else {
                neighbors.add(this.nodes[boxCord[0]][dim[1] - 1]);
            }
            if (boxCord[1] < dim[1] - 1) {
                neighbors.add(this.nodes[boxCord[0]][boxCord[1] + 1]);
            } else {
                neighbors.add(this.nodes[dim[0] - 1][boxCord[0]]);
            }
        } else {
            if (boxCord[0] > 0) {
                neighbors.add(this.nodes[boxCord[0] - 1][boxCord[1]]);
            }
            if (boxCord[0] < dim[0] - 1) {
                neighbors.add(this.nodes[boxCord[0] + 1][boxCord[1]]);
            }
            if (boxCord[1] > 0) {
                neighbors.add(this.nodes[boxCord[0]][boxCord[1] - 1]);
            }
            if (boxCord[1] < dim[1] - 1) {
                neighbors.add(this.nodes[boxCord[0]][boxCord[1] + 1]);
            }
        }
        neighbors.remove(node.getParent()); //may not be needed

        return neighbors;
    }

    public Node[][] getNodes() {

        return nodes;
    }
}