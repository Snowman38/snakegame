package server.game.usables;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoordinateTests {

    Coordinate coord = new Coordinate(0, 0, true);

    @Test
    public void testGetY() {

        assertEquals(coord.getY(), 0);
    }

    @Test
    public void testSetY() {

        coord.setY(5);
        assertEquals(coord.getY(), 5);
    }

    @Test
    public void testGetX() {

        assertEquals(coord.getX(), 0);
    }

    @Test
    public void testSetX() {

        coord.setX(5);
        assertEquals(coord.getX(), 5);
    }

    @Test
    public void testAddX() {

        coord.addX(1);
        assertEquals(coord.getX(), 1);
    }

    @Test
    public void testAddY() {

        coord.addY(1);
        assertEquals(coord.getY(), 1);
    }

    @Test
    public void testIsWalkable() {

        assertTrue(coord.isWalkable());
    }

    @Test
    public void testToIntArray() {

        coord.setY(2);
        int[] arr = coord.toIntArray();
        assertEquals(arr[0], 0);
        assertEquals(arr[1], 2);
    }

    @Test
    public void equals() {

        Coordinate coord2 = new Coordinate(0, 0, true);
        assertTrue(coord.equals(coord2));
    }

    @Test
    public void testHashCode() {

        Coordinate coord2 = new Coordinate(0, 0, true);
        assertEquals(coord.hashCode(), coord2.hashCode());
    }

    @Test
    public void testToString() {

        assertEquals(coord.toString(), "[0, 0]");
    }
}
