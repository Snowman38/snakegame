package server.ai.util;

import server.ai.pathfinding.Node;

/**
 * A utility class used in the pathfinding package
 *
 * @author Daniel Batchford
 */
public class Util {

    /**
     * @param a       The 2 length int array representing the start coordinate (e.g. [x1,y1])
     * @param b       The 2 length int array representing the end coordinate (e.g. [x2,y2])
     * @param xDim    The x dimension of the grid
     * @param yDim    The y dimension of the grid
     * @param wrapped Whether to allow distance through the edges of the grid
     * @return The Manhattan distance between coordinates a and b
     * @author Daniel Batchford
     */
    public static int getDistance(int[] a, int[] b, int xDim, int yDim, boolean wrapped) {

        int dx = Math.abs(a[0] - b[0]);
        int dy = Math.abs(a[1] - b[1]);
        return wrapped ? Math.min(dx, xDim - dx) + Math.min(dy, yDim - dy) : dx + dy;
    }

    /**
     * @param nA      The node representing the start coordinate
     * @param nB      The node representing the end coordinate
     * @param xDim    The x dimension of the grid
     * @param yDim    The y dimension of the grid
     * @param wrapped Whether to allow distance through the edges of the grid
     * @return The Manhattan distance between nodes nA and nB
     * @author Daniel Batchford
     */
    public static float getDistance(Node nA, Node nB, int xDim, int yDim, boolean wrapped) {

        return getDistance(nA.toIntArray(), nB.toIntArray(), xDim, yDim, wrapped);
    }

    /**
     * @param a    The 2 length int array representing the start coordinate (e.g. [x1,y1])
     * @param b    The 2 length int array representing the end coordinate (e.g. [x2,y2])
     * @param xDim The x dimension of the grid
     * @param yDim The y dimension of the grid
     * @return The Manhattan distance between coordinates a and b
     * @author Daniel Batchford
     */
    public static float getDistance(int[] a, int[] b, int xDim, int yDim) {

        return getDistance(a, b, xDim, yDim, false);
    }

    /**
     * @param nA   The node representing the start coordinate
     * @param nB   The node representing the end coordinate
     * @param xDim The x dimension of the grid
     * @param yDim The y dimension of the grid
     * @return The Manhattan distance between nodes nA and nB
     * @author Daniel Batchford
     */
    public static float getDistance(Node nA, Node nB, int xDim, int yDim) {

        return getDistance(nA, nB, xDim, yDim, false);
    }

    /**
     * @param a The 2 length int array representing the start coordinate (e.g. [x1,y1])
     * @param b The 2 length int array representing the end coordinate (e.g. [x2,y2])
     * @return The Manhattan distance between coordinates a and b
     * @author Daniel Batchford
     */
    public static float getDistance(int[] a, int[] b) {

        return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
    }

    /**
     * @param a The 2 length int array representing coordinate a (e.g. [x1,y1])
     * @param b The 2 length int array representing coordinate b (e.g. [x2,y2])
     * @return [x1 - x2, x2 - y2]
     * @author Daniel Batchford
     */
    public static int[] subtract2DIntArray(int[] a, int[] b) {

        if (a == null || b == null) {
            return null;
        }
        if ((a.length != 2) || (b.length != 2)) {
            return null;
        }
        return new int[]{a[0] - b[0], a[1] - b[1]};
    }

    /**
     * If lengths of a and b are not 2, null is returned
     *
     * @param a 2d vector a
     * @param b 2d vector b
     * @return Dot product a.b
     * @author Daniel Batchford
     */
    public static int[] dotProduct2DArray(int[] a, int[] b) {

        if (a == null || b == null) {
            return null;
        }
        if ((a.length != 2) || (b.length != 2)) {
            return null;
        }
        return new int[]{a[0] * b[0], a[1] * b[1]};
    }
}
