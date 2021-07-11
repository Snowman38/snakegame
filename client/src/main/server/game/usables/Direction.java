package server.game.usables;

/**
 * Enum class used to hold cardinal directions
 */
public enum Direction {
    UP, DOWN, RIGHT, LEFT;

    static {
        UP.opposite = DOWN;
        DOWN.opposite = UP;
        LEFT.opposite = RIGHT;
        RIGHT.opposite = LEFT;
    }

    private Direction opposite;

    /**
     * @return the opposite of the current direction
     */
    public Direction getOpposite() {

        return opposite;
    }
}