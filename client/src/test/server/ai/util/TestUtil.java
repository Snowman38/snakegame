package server.ai.util;

import org.junit.jupiter.api.Test;
import server.ai.pathfinding.Node;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TestUtil {

    @Test
    public void testGetDistanceA1() {

        int distance = Util.getDistance(new int[]{2, 0}, new int[]{3, 1}, 20, 20, false);
        assertEquals(distance, 2);
    }

    @Test
    public void testGetDistanceA2() {

        int distance = Util.getDistance(new int[]{0, 0}, new int[]{2, 2}, 3, 3, true);
        assertEquals(distance, 2);
    }

    @Test
    public void testGetDistanceB() {

        Node nA = new Node(new int[]{0, 0}, true);
        Node nB = new Node(new int[]{3, 5}, true);
        float distance = Util.getDistance(nA, nB, 20, 20, false);
        assertEquals(distance, 8.0f);
    }

    @Test
    public void testGetDistanceC() {

        float distance = Util.getDistance(new int[]{2, 0}, new int[]{3, 1}, 20, 20);
        assertEquals(distance, 2.0f);
    }

    @Test
    public void testGetDistanceD() {

        Node nA = new Node(new int[]{0, 0}, true);
        Node nB = new Node(new int[]{19, 19}, true);
        float distance = Util.getDistance(nA, nB, 20, 20);
        assertEquals(distance, 38.0f);
    }

    @Test
    public void testGetDistanceE() {

        float distance = Util.getDistance(new int[]{190, 20}, new int[]{0, 5});
        assertEquals(distance, 205);
    }

    @Test
    public void testSubtract2DIntArray1() {

        assertNull(Util.subtract2DIntArray(null, new int[]{5, 5}));
    }

    @Test
    public void testSubtract2DIntArray2() {

        assertNull(Util.subtract2DIntArray(new int[]{1, 2, 3}, new int[]{1, 2}));
    }

    @Test
    public void testSubtract2DIntArray3() {

        assertTrue(Arrays.equals(Util.subtract2DIntArray(new int[]{9, 8}, new int[]{4, 3}), new int[]{5, 5}));
    }

    @Test
    public void testDotProduct2DIntArray1() {

        assertNull(Util.dotProduct2DArray(new int[]{1, 6, 7}, null));
    }

    @Test
    public void testDotProduct2DIntArray2() {

        assertNull(Util.dotProduct2DArray(new int[]{1, 6, 7}, new int[]{9, 0}));
    }

    @Test
    public void testDotProduct2DIntArray3() {

        assertTrue(Arrays.equals(Util.dotProduct2DArray(new int[]{0, 2}, new int[]{3, 5}), new int[]{0, 10}));
    }

    @Test
    public void testDotProduct2DIntArray4() {

        assertTrue(Arrays.equals(Util.dotProduct2DArray(new int[]{20, 5}, new int[]{-1, 1}), new int[]{-20, 5}));
    }
}