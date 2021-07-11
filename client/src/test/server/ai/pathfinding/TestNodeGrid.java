package server.ai.pathfinding;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestNodeGrid {

    NodeGrid grid;

    @Test
    public void testInvalidDimensions1() {

        PathFindingException thrown = assertThrows(
                PathFindingException.class,
                () -> grid = new NodeGrid(new int[]{10}),
                "Expected PathFindingException but none was thrown"
        );
    }

    @Test
    public void testInvalidDimensions2() {

        PathFindingException thrown = assertThrows(
                PathFindingException.class,
                () -> grid = new NodeGrid(new int[]{10, 20, 30}),
                "Expected PathFindingException but none was thrown"
        );
    }

    @Test
    public void testNullDimensions() {

        PathFindingException thrown = assertThrows(
                PathFindingException.class,
                () -> grid = new NodeGrid(null),
                "Expected PathFindingException but none was thrown"
        );
    }

    @Test
    public void testNegDimensions1() {

        PathFindingException thrown = assertThrows(
                PathFindingException.class,
                () -> grid = new NodeGrid(new int[]{-1, 20}),
                "Expected PathFindingException but none was thrown"
        );
    }

    @Test
    public void testNegDimensions2() {

        PathFindingException thrown = assertThrows(
                PathFindingException.class,
                () -> grid = new NodeGrid(new int[]{20, -1}),
                "Expected PathFindingException but none was thrown"
        );
    }

}
