package server.ai.pathfinding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestNode {

    @Test
    public void testGetNode() {

        Node node = new Node(new int[]{5, 6}, false);
        assertArrayEquals(node.getCord(), new int[]{5, 6});
    }

    @Test
    public void testSetParent() {

        Node node = new Node(new int[]{5, 6}, false);
        node.setParent(new Node(new int[]{5, 7}));
        assertEquals(node.getParent(), new Node(new int[]{5, 7}));
    }
}
