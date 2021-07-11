package server.ai.pathfinding;

import java.util.Arrays;

/**
 * A node representing a grid square. Each node has a reference to a parent node as well as a coordinate location and f g values
 * for heuristic pathfinding.
 *
 * @author Daniel Batchford
 */
public class Node {

    private final int[] cord;
    private boolean walkable;
    private Node parent;
    private float f;
    private float g;

    /**
     * @param cord     The 2 length int array representing the coordinate, in form [x.y]
     * @param walkable Whether this coordinate is walkable
     * @author Daniel Batchford
     */
    public Node(int[] cord, boolean walkable) {

        this.cord = cord;
        this.walkable = walkable;
    }

    Node(int x, int y, boolean walkable) {

        this.cord = new int[]{x, y};
    }

    Node(int x, int y) {

        this.cord = new int[]{x, y};
        this.walkable = true;
    }

    Node(int[] cord) {

        this.cord = cord;
        this.walkable = true;
    }

    public boolean isWalkable() {

        return walkable;
    }

    public void setWalkable(boolean walkable) {

        this.walkable = walkable;
    }

    /**
     * @return a coordinate as a 2 length int array of form [x,y]
     * @author Daniel Batchford
     */
    public int[] toIntArray() {

        return cord;
    }

    public float getF() {

        return f;
    }

    public void setF(float f) {

        this.f = f;
    }

    public float getG() {

        return g;
    }

    public void setG(float g) {

        this.g = g;
    }

    public Node getParent() {

        return parent;
    }

    public void setParent(Node parent) {

        this.parent = parent;
    }

    public int[] getCord() {

        return this.cord;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Arrays.equals(cord, node.cord);
    }

    @Override
    public int hashCode() {

        return Arrays.hashCode(cord);
    }

    @Override
    public String toString() {

        return "(" + cord[0] + "," + cord[1] + ")";
    }

}