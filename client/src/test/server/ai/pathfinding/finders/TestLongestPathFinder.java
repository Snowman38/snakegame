package server.ai.pathfinding.finders;

import org.junit.jupiter.api.Test;
import server.ai.pathfinding.NoPathFoundException;
import server.ai.pathfinding.NodeGrid;
import server.ai.pathfinding.PathFindingException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
public class TestLongestPathFinder {

    Finder finder;
    NodeGrid grid;

    @Test
    public void testSimplePath() throws NoPathFoundException, PathFindingException {

        finder = new LongestPathFinder(20, 20, false);
        grid = new NodeGrid(new int[]{20, 20});

        int[] start = new int[]{0, 0};
        int[] end = new int[]{19, 19};

        List<int[]> path = finder.findPath(start, end, grid);
        assertArrayEquals(path.get(0), start);
        assertArrayEquals(path.get(path.size() - 1), end);
    }

    @Test
    public void testBlockedPath() throws PathFindingException {

        finder = new LongestPathFinder(20, 20, false);
        grid = new NodeGrid(new int[]{20, 20});

        for (int i = 0; i < 20; i++) {
            grid.setWalkable(new int[]{i, 5}, false);
        }

        int[] start = new int[]{0, 0};
        int[] end = new int[]{19, 19};

        assertThrows(
                NoPathFoundException.class,
                () -> finder.findPath(start, end, grid),
                "Expected NoPathFoundException but none was thrown"
        );
    }

    @Test
    public void testSemiBlockedPath() throws PathFindingException, NoPathFoundException {

        finder = new LongestPathFinder(20, 20, false);
        grid = new NodeGrid(new int[]{20, 20});

        for (int i = 1; i < 20; i++) {
            grid.setWalkable(new int[]{i, 5}, false);
        }

        int[] start = new int[]{0, 0};
        int[] end = new int[]{19, 19};

        List<int[]> path = finder.findPath(start, end, grid);

        boolean found = false;
        for (int[] cord : path) {
            if (Arrays.equals(cord, new int[]{0, 5})) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testComplexPath() throws NoPathFoundException, PathFindingException {

        finder = new LongestPathFinder(20, 20, false);
        grid = new NodeGrid(new int[]{2, 2});

        int[] start = new int[]{0, 0};
        int[] end = new int[]{1, 1};

        List<int[]> path = finder.findPath(start, end, grid);

        assertArrayEquals(path.get(0), new int[]{0, 0});
        assertArrayEquals(path.get(path.size() - 1), new int[]{1, 1});
        assertTrue(Arrays.equals(path.get(1), new int[]{1, 0}) || Arrays.equals(path.get(1), new int[]{0, 1}));

    }

    @Test
    public void testNotDisjointPath() throws PathFindingException, NoPathFoundException {

        finder = new LongestPathFinder(20, 20, false);
        grid = new NodeGrid(new int[]{50, 50});

        int[] start = new int[]{0, 0};
        int[] end = new int[]{49, 49};

        List<int[]> path = finder.findPath(start, end, grid);

        for (int i = 1; i < path.size(); i++) {
            int[] prev = path.get(i - 1);
            int[] curr = path.get(i);

            int distance = Math.abs(prev[0] - curr[0]) + Math.abs(prev[1] - curr[1]);

            assertEquals(1, distance);
        }
    }

    @Test
    public void testNullOriginNode1() throws PathFindingException {

        finder = new LongestPathFinder(20, 20, false);
        grid = new NodeGrid(new int[]{20, 20});

        int[] start = new int[]{0, 0};
        int[] end = null;

        assertThrows(
                PathFindingException.class,
                () -> finder.findPath(start, end, grid),
                "Expected PathFindingException but none was thrown"
        );
    }

    @Test
    public void testNullOriginNode2() throws PathFindingException {

        finder = new LongestPathFinder(20, 20, false);
        grid = new NodeGrid(new int[]{20, 20});

        int[] start = new int[]{0, 0};
        int[] end = null;

        assertThrows(
                PathFindingException.class,
                () -> finder.findPath(start, end, grid),
                "Expected PathFindingException but none was thrown"
        );
    }
}
