package server.ai;

import logging.Logger;
import server.ai.pathfinding.NoPathFoundException;
import server.ai.pathfinding.NodeGrid;
import server.ai.pathfinding.PathFindingException;
import server.ai.pathfinding.finders.AStarFinder;
import server.ai.pathfinding.finders.Finder;
import server.ai.pathfinding.finders.LongestPathFinder;
import server.ai.pathfinding.finders.TorusAStarFinder;
import server.ai.util.Util;
import server.game.usables.Coordinate;
import server.game.usables.Direction;
import server.interfaces.AIIF;

import java.util.*;

/**
 * This class controls AI behaviour. It contains 3 main behaviours, chaseFruit(), chaseTail() and chaseEnemyMotives().
 * <p>
 * chaseFruit() attempts to find a path from the ai head to the closest fruit.
 * chaseTail() aims to find a sufficiently long path from the ai head to it's own tail
 * chaseEnemyMotives() selects the shortest of a path to a fruit and a path to a target square near the enemy's head.
 * The target square is selected based off the player's current position and direction.
 * <p>
 * In the 3 modes, EASY, MEDIUM and HARD, different behaviours are selected.
 * <p>
 * In EASY MODE:
 * chaseFruit() is used and the ai ignores wrapping around the board edges.
 * There is then a 50% chance that the ai follows this path and a 50% chance of using chaseTail()
 * <p>
 * In MEDIUM MODE:
 * chaseFruit() is used and the ai ignores wrapping around the board edges.
 * There is then an 80% chance that the ai follows this path and a 20% chance of using chaseTail()
 * <p>
 * In HARD MODE:
 * chaseEnemyMotives() is used and the ai considers wrapping around the board edges. If a path to a fruit is selected, it
 * will never ignore this path (100% acceptance chance)
 *
 * @author Daniel Batchford
 */
public class AIHandler implements AIIF {

    private final Difficulty difficulty;
    private final float acceptChance;
    private final boolean useWrappedDistance;
    private final Finder aStarFinder;
    private final Finder longestPathFinder;

    private final Random random;
    private final int xDim;
    private final int yDim;
    private LinkedList<Coordinate> snakeAI;
    private LinkedList<Coordinate> snakeP;
    private LinkedList<int[]> path;
    private List<Coordinate> fruits;
    private Coordinate[][] board;
    private NodeGrid grid;

    /**
     * @param xDim       The X dimension of the grid
     * @param yDim       The Y dimension of the grid
     * @param difficulty The selected difficulty
     *                   AIHandler constructor
     * @author Daniel Batchford
     */
    public AIHandler(int xDim, int yDim, Difficulty difficulty) {

        this.difficulty = difficulty;

        this.random = new Random();

        this.xDim = xDim;
        this.yDim = yDim;

        switch (this.difficulty) {
            case EASY -> {
                this.acceptChance = 0.50f;
                this.aStarFinder = new AStarFinder(xDim, yDim);
                this.useWrappedDistance = false;
            }
            case MEDIUM -> {
                this.acceptChance = 0.85f;
                this.aStarFinder = new AStarFinder(xDim, yDim);
                this.useWrappedDistance = false;
            }
            case HARD -> {
                this.acceptChance = 1.00f;
                this.aStarFinder = new TorusAStarFinder(xDim, yDim);
                this.useWrappedDistance = true;
            }
            default -> {
                this.acceptChance = 0.0f;
                this.aStarFinder = new AStarFinder(xDim, yDim);
                this.useWrappedDistance = false;
            }
        }
        this.longestPathFinder = new LongestPathFinder(xDim, yDim, this.useWrappedDistance);
    }

    // Finds the cord on a path where the distance to it from both the ai and enemy is the smallest (With a 2 buffer for the enemy)
    private static int[] getTargetSquareOnPath(List<int[]> path, int[] aiHead, int[] enemyHead, int xDim, int yDim, boolean useWrapped) {

        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(
                cord -> Math.abs(Util.getDistance(aiHead, cord, xDim, yDim, useWrapped) - Util.getDistance(enemyHead, cord, xDim, yDim, useWrapped))
        ));

        int crashBuffer = 2;
        if (path.size() > crashBuffer) {
            path.subList(0, crashBuffer).clear();
        }
        queue.addAll(path);
        return queue.poll();
    }

    // Finds a path from the head of the ai snake to the closest fruit
    private Direction chaseFruit() {

        this.grid = initNodeGrid(board);

        int[] headCord = snakeAI.getFirst().toIntArray();
        int[] fruitCord = getClosestFruit(headCord, fruits);

        setWalkableAlongSnake(this.snakeAI, false);
        setWalkableAlongSnake(this.snakeP, false);

        // Set tail square to walkable (needed for pathfinding)
        grid.setWalkable(headCord, true);
        grid.setWalkable(fruitCord, true);

        try {
            this.path = (LinkedList<int[]>) aStarFinder.findPath(headCord, fruitCord, grid);
            if (acceptFruitPath()) {

                // Select this path with a random probability p, based off difficulty setting
                if (random.nextFloat() < this.acceptChance) {
                    return getDirectionFromPath();
                }
            }
            return chaseTail();
        } catch (PathFindingException e) {
            e.printStackTrace();

        } catch (NoPathFoundException e) {
            return chaseTail();
        }

        return null;
    }

    /* Finds a sufficiently long path from the head of the ai to it's own tail (Survival mode). This longest path leads
    the ai to "waste time" while it waits for a valid path to an apple
     */
    private Direction chaseTail() {

        this.grid = initNodeGrid(board);

        setWalkableAlongSnake(this.snakeAI, false);
        setWalkableAlongSnake(this.snakeP, false);

        int[] headCord = snakeAI.getFirst().toIntArray();
        int[] tailCord = snakeAI.getLast().toIntArray();

        // Set head and tail squares to walkable (needed for pathfinding)
        grid.setWalkable(headCord, true);
        grid.setWalkable(tailCord, true);

        try {
            this.path = (LinkedList<int[]>) longestPathFinder.findPath(headCord, tailCord, grid);

            // If the tail path found goes directly from the head to the tail. This is possible as both the head
            // and tail are declared walkable for the pathfinding
            if (this.snakeAI.size() == 2 && this.path.size() == 2) {
                return null;
            }
            return getDirectionFromPath();
        } catch (PathFindingException e) {
            e.printStackTrace();
        } catch (NoPathFoundException e) {
            Logger.debug("No path found from head to tail, returning null");
        }
        return null;
    }

    // Aims to cut off the player from his objective of obtaining fruit, with an equal weighting towards also obtaining fruit
    private Direction chaseEnemyMotives() {

        List<int[]> enemyPathToFruit = getEnemyOptimalPath();

        // Couldnt predict a good path that the enemy could take (Enemy is likely trapped and going to die)
        if ((enemyPathToFruit == null)) {
            return chaseFruit();
        }

        this.grid = initNodeGrid(board);

        int[] aiHeadCord = snakeAI.getFirst().toIntArray();
        int[] enemyHeadCord = snakeP.getFirst().toIntArray();

        // Mark square's as non walkable along the length of both snakes
        setWalkableAlongSnake(this.snakeAI, false);
        setWalkableAlongSnake(this.snakeP, false);

        // Set head coordinates as walkable, preventing the pathfinding from finding a path to a non walkable square
        grid.setWalkable(aiHeadCord, true);
        grid.setWalkable(enemyHeadCord, true);

        try {
            int[] fruitCord = getClosestFruit(aiHeadCord, fruits);
            int[] targetCutOffCord = getTargetSquareOnPath(enemyPathToFruit, aiHeadCord, enemyHeadCord, xDim, yDim, this.useWrappedDistance);

            // Find a path to the front of the enemy, as well as a path to the closest fruit
            LinkedList<int[]> pathToEnemyCutoff = (LinkedList<int[]>) aStarFinder.findPath(aiHeadCord, targetCutOffCord, this.grid);
            LinkedList<int[]> pathToClosestFruit = (LinkedList<int[]>) aStarFinder.findPath(aiHeadCord, fruitCord, grid);

            // Both paths are invalid, survive by chasing the snake's own tail
            if (pathToClosestFruit == null && pathToEnemyCutoff == null) {
                return chaseTail();
            }

            // If there is no valid path to the fruit, use the path to the front of the enemy
            if (pathToClosestFruit == null) {
                this.path = pathToEnemyCutoff;
                return getDirectionFromPath();
            }

            // If there is no valid path to the enemy, use the path to the closest fruit
            if (pathToEnemyCutoff == null) {
                this.path = pathToClosestFruit;
                return getDirectionFromPath();

            }

            // Use the closest of both paths
            this.path = (pathToClosestFruit.size() < enemyPathToFruit.size()) ? pathToClosestFruit : pathToEnemyCutoff;
            return getDirectionFromPath();

        } catch (PathFindingException e) {
            e.printStackTrace();

        } catch (NoPathFoundException e) {
            return chaseFruit();
        }
        return null;
    }

    /* Checks to see if found fruit path would lead to the snake unable to find a path to it's own tail once
     it has travelled along the fruit path. */
    private boolean acceptFruitPath() {

        // Set fruit path as non walkable
        for (int[] cord : path) {
            this.grid.setWalkable(cord, false);
        }

        // Set tail square to walkable (needed for pathfinding)
        int[] tailCord = snakeAI.getLast().toIntArray();
        this.grid.setWalkable(tailCord, true);

        // Set apple square to walkable
        this.grid.setWalkable(path.getLast(), true);

        try {
            aStarFinder.findPath(path.getLast(), snakeAI.getLast().toIntArray(), this.grid);
        } catch (PathFindingException e) {
            Logger.error("Error while validating apple path");
            e.printStackTrace();
            return false;
        } catch (NoPathFoundException e) {
            return false;
        }

        return true;
    }

    // Marks each coordinate along a list as walkable (true or false)
    private void setWalkableAlongSnake(LinkedList<Coordinate> list, boolean walkable) {

        list.forEach((coordinate -> this.grid.setWalkable(coordinate.toIntArray(), walkable)));
    }

    // Returns the closest fruit from the ai's head
    private int[] getClosestFruit(int[] head, List<Coordinate> fruits) {

        if (fruits.size() == 0) {
            Logger.error("Cannot find closest fruit as fruit list size was 0");
        }

        Coordinate minFruit = fruits.get(0);
        float minDistance = Util.getDistance(minFruit.toIntArray(), head, xDim, yDim, this.useWrappedDistance);

        for (Coordinate fruit : fruits) {

            float currDistance = Util.getDistance(fruit.toIntArray(), head, xDim, yDim, this.useWrappedDistance);
            if (currDistance < minDistance) {
                minFruit = new Coordinate(fruit);
                minDistance = currDistance;
            }
        }

        return minFruit.toIntArray();
    }

    // Get's the shortest path the enemy could take to a fruit. It assumes the enemy player follow the shortest path
    private List<int[]> getEnemyOptimalPath() {

        this.grid = initNodeGrid(board);

        setWalkableAlongSnake(this.snakeAI, false);
        setWalkableAlongSnake(this.snakeP, false);

        int[] playerHeadCord = snakeP.getFirst().toIntArray();
        int[] closestFruitToPlayer = getClosestFruit(playerHeadCord, this.fruits);
        this.grid.setWalkable(playerHeadCord, true);
        this.grid.setWalkable(closestFruitToPlayer, true);

        try {
            return aStarFinder.findPath(playerHeadCord, closestFruitToPlayer, grid);

        } catch (PathFindingException e) {
            e.printStackTrace();
        } catch (NoPathFoundException ignored) {
        }
        return null;
    }

    // Initialise a NodeGrid from a 2d array of coordinates
    private NodeGrid initNodeGrid(Coordinate[][] board) {

        int[] dimensions = new int[]{board.length, board[0].length};
        try {
            NodeGrid nodeGrid = new NodeGrid(dimensions);

            for (int x = 0; x < dimensions[0]; x++) {
                for (int y = 0; y < dimensions[1]; y++) {
                    Coordinate curr = board[x][y];
                    nodeGrid.setWalkable(curr.toIntArray(), curr.isWalkable());
                }
            }

            return nodeGrid;
        } catch (PathFindingException e) {
            Logger.error("Something went wrong");
            e.printStackTrace();
        }

        return null;
    }

    // Returns the direction needed to travel from the 1st to the 2nd coordinate on a path, accounting for edge wrapping
    private Direction getDirectionFromPath() {

        if (this.path.size() == 0) {
            Logger.error("Path cannot resolve direction as length of path was 0");
            return null;
        }

        if (this.path.size() == 1) {
            return null;
        }

        int[] headCord = path.get(0);
        int[] nextCord = path.get(1);

        int dx = nextCord[0] - headCord[0];
        int dy = nextCord[1] - headCord[1];

        // Resolve a direction from dx and dy, including edge cases
        if ((dx == 0) && (dy == 1 || dy == -(yDim - 1))) {
            return Direction.DOWN;
        }
        if ((dx == 0) && (dy == -1 || dy == yDim - 1)) {
            return Direction.UP;
        }
        if ((dx == -1 || dx == xDim - 1) && (dy == 0)) {
            return Direction.LEFT;
        }
        if ((dx == 1 || dx == -(xDim - 1)) && (dy == 0)) {
            return Direction.RIGHT;
        }
        return null;
    }

    /**
     * @param board   The Coordinate[][] representing the current board
     * @param snakeAI The list representing the ai's snake
     * @param snakeP  The list representing the player's snake
     * @param fruits  The list of the current fruit coordinates
     * @return A direction representing the next AI movement direction
     * @author Daniel Batchford
     */
    public Direction getNextMove(Coordinate[][] board, List<Coordinate> snakeAI, List<Coordinate> snakeP, List<Coordinate> fruits) {

        this.board = board;
        this.snakeAI = (LinkedList<Coordinate>) snakeAI;
        this.snakeP = (LinkedList<Coordinate>) snakeP;
        this.fruits = fruits;
        this.path = new LinkedList<>();

        if (this.difficulty == Difficulty.HARD) {
            return chaseEnemyMotives();
        }

        return chaseFruit();
    }
}