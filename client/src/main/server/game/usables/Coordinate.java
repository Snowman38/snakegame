package server.game.usables;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * A class representing an x, y coordinate and it's walkable status (Used for AI)
 */
public class Coordinate implements Serializable {

    private final boolean walkable;
    private int x;
    private int y;

    /**
     * Here, walkable defaults to true
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Coordinate(int x, int y) {

        this.x = x;
        this.y = y;
        this.walkable = true;
    }

    /**
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param walkable whether this coordinate is walkable
     */
    public Coordinate(int x, int y, boolean walkable) {

        this.x = x;
        this.y = y;
        this.walkable = walkable;
    }

    /**
     * Here, walkable defaults to true
     *
     * @param cord a 2 length int array representing [x,y] coordinates
     */
    public Coordinate(int[] cord) {

        this.x = cord[0];
        this.y = cord[1];
        this.walkable = true;
    }

    /**
     * @param cord     a 2 length int array representing [x,y] coordinates
     * @param walkable whether this coordinate is walkable
     */
    public Coordinate(int[] cord, boolean walkable) {

        this.x = cord[0];
        this.y = cord[1];
        this.walkable = walkable;
    }

    /**
     * Deep Copy Constructor
     *
     * @param coordinate the coordinate to copy
     * @param walkable   whether this coordinate is walkable
     */
    public Coordinate(Coordinate coordinate, boolean walkable) {

        this.x = coordinate.getX();
        this.y = coordinate.getY();
        this.walkable = walkable;
    }

    /**
     * Here, walkable defaults to true
     *
     * @param coordinate the coordinate to copy
     */
    public Coordinate(Coordinate coordinate) {

        this.x = coordinate.getX();
        this.y = coordinate.getY();
        this.walkable = true;
    }

    /* Getters and Setters */

    public int getY() {

        return y;
    }

    public void setY(int y) {

        this.y = y;
    }

    public int getX() {

        return x;
    }

    public void setX(int x) {

        this.x = x;
    }

    /**
     * Increments the x coordinate by i
     *
     * @param i the increment
     */
    public void addX(int i) {

        this.x += i;
    }

    /**
     * Increments the y coordinate by i
     *
     * @param i the increment
     */
    public void addY(int i) {

        this.y += i;
    }

    public boolean isWalkable() {

        return this.walkable;
    }

    /**
     * @return the coordinate as a 2 length int array in form [x,y]
     */
    public int[] toIntArray() {

        return new int[]{this.x, this.y};
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y);
    }

    /**
     * @return the coordinate in form "[x,y]"
     */
    @Override
    public String toString() {

        return Arrays.toString(this.toIntArray());
    }
}
