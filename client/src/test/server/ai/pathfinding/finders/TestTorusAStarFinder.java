package server.ai.pathfinding.finders;

import org.junit.jupiter.api.Test;
import server.ai.pathfinding.NoPathFoundException;
import server.ai.pathfinding.NodeGrid;
import server.ai.pathfinding.PathFindingException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
public class TestTorusAStarFinder {

    Finder finder;
    NodeGrid grid;

    @Test
    public void testSimplePath() throws NoPathFoundException, PathFindingException {

        finder = new TorusAStarFinder(20, 20);
        grid = new NodeGrid(new int[]{20, 20});

        int[] start = new int[]{0, 0};
        int[] end = new int[]{19, 19};

        List<int[]> path = finder.findPath(start, end, grid);
        assertArrayEquals(path.get(0), start);
        assertArrayEquals(path.get(path.size() - 1), end);
    }

    @Test
    public void testBlockedPath1() throws PathFindingException, NoPathFoundException {

        finder = new TorusAStarFinder(20, 20);
        grid = new NodeGrid(new int[]{20, 20});

        for (int i = 0; i < 20; i++) {
            grid.setWalkable(new int[]{6, i}, false);
        }

        int[] start = new int[]{0, 0};
        int[] end = new int[]{19, 19};

        assertNotNull(finder.findPath(start, end, grid));
    }

    @Test
    public void testBlockedPath2() throws PathFindingException, NoPathFoundException {

        finder = new TorusAStarFinder(20, 20);
        grid = new NodeGrid(new int[]{20, 20});

        for (int i = 0; i < 20; i++) {
            grid.setWalkable(new int[]{i, 5}, false);
        }

        int[] start = new int[]{0, 0};
        int[] end = new int[]{19, 19};

        assertNotNull(finder.findPath(start, end, grid));
    }

    @Test
    public void testSemiBlockedPath() throws PathFindingException, NoPathFoundException {

        finder = new TorusAStarFinder(20, 20);
        grid = new NodeGrid(new int[]{20, 20});

        for (int i = 1; i < 20; i++) {
            grid.setWalkable(new int[]{i, 5}, false);
        }

        int[] start = new int[]{0, 0};
        int[] end = new int[]{19, 19};

        List<int[]> path = finder.findPath(start, end, grid);

        assertNotNull(path);

    }

    @Test
    public void testComplexPath() throws NoPathFoundException, PathFindingException {

        finder = new TorusAStarFinder(20, 20);
        grid = new NodeGrid(new int[]{2, 2});

        int[] start = new int[]{0, 0};
        int[] end = new int[]{1, 1};

        List<int[]> path = finder.findPath(start, end, grid);

        assertArrayEquals(path.get(0), new int[]{0, 0});
        assertArrayEquals(path.get(path.size() - 1), new int[]{1, 1});
        assertTrue(Arrays.equals(path.get(1), new int[]{1, 0}) || Arrays.equals(path.get(1), new int[]{0, 1}));

    }

    @Test
    public void testNotDisjointPath1() throws PathFindingException, NoPathFoundException {

        finder = new TorusAStarFinder(20, 20);
        int gridWidth = 50;
        grid = new NodeGrid(new int[]{gridWidth, gridWidth});

        int[] start = new int[]{0, 0};
        int[] end = new int[]{gridWidth - 1, gridWidth - 1};

        List<int[]> path = finder.findPath(start, end, grid);

        for (int i = 1; i < path.size(); i++) {
            int[] prev = path.get(i - 1);
            int[] curr = path.get(i);

            int distance = Math.abs(prev[0] - curr[0]) + Math.abs(prev[1] - curr[1]);
            assertTrue(distance == gridWidth - 1 || distance == 1);
        }
    }

    @Test
    public void testNotDisjointPath2() throws PathFindingException, NoPathFoundException {

        finder = new TorusAStarFinder(20, 20);
        int gridWidth = 20;
        grid = new NodeGrid(new int[]{gridWidth, gridWidth});

        int[] start = new int[]{0, 0};
        int[] end = new int[]{gridWidth - 1, gridWidth - 1};

        List<int[]> path = finder.findPath(start, end, grid);

        for (int i = 1; i < path.size(); i++) {
            int[] prev = path.get(i - 1);
            int[] curr = path.get(i);

            int distance = Math.abs(prev[0] - curr[0]) + Math.abs(prev[1] - curr[1]);
            assertTrue(distance == gridWidth - 1 || distance == 1);
        }
    }

    @Test
    public void testNotDisjointPath3() throws PathFindingException, NoPathFoundException {

        finder = new TorusAStarFinder(20, 20);
        int gridWidth = 100;
        grid = new NodeGrid(new int[]{gridWidth, gridWidth});

        int[] start = new int[]{0, 0};
        int[] end = new int[]{gridWidth - 1, gridWidth - 1};

        List<int[]> path = finder.findPath(start, end, grid);

        for (int i = 1; i < path.size(); i++) {
            int[] prev = path.get(i - 1);
            int[] curr = path.get(i);

            int distance = Math.abs(prev[0] - curr[0]) + Math.abs(prev[1] - curr[1]);
            assertTrue(distance == gridWidth - 1 || distance == 1);
        }
    }

    @Test
    public void testNullOriginNode1() throws PathFindingException {

        finder = new TorusAStarFinder(20, 20);
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

        finder = new TorusAStarFinder(20, 20);
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
    public void testTwoLengthPath() throws NoPathFoundException, PathFindingException {

        finder = new TorusAStarFinder(20, 20);
        grid = new NodeGrid(new int[]{20, 20});

        int[] start = new int[]{5, 6};
        int[] end = new int[]{5, 7};

        List<int[]> path = finder.findPath(start, end, grid);

        assertArrayEquals(new int[]{5, 6}, path.get(0));
        assertArrayEquals(new int[]{5, 7}, path.get(1));
    }

    @Test
    public void testTenLengthPath() throws NoPathFoundException, PathFindingException {

        finder = new TorusAStarFinder(20, 20);
        grid = new NodeGrid(new int[]{20, 20});

        int[] start = new int[]{0, 6};
        int[] end = new int[]{9, 6};

        List<int[]> path = finder.findPath(start, end, grid);

        for (int i = 0; i < 10; i++) {
            assertArrayEquals(new int[]{i, 6}, path.get(i));
        }
    }

}
