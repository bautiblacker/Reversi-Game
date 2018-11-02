package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    private Point origin = new Point(0, 0);
    private Direction up = Direction.UP;
    private Direction down = Direction.DOWN;
    private Direction left = Direction.LEFT;
    private Direction right = Direction.RIGHT;
    private Direction upLeft = Direction.UP_LEFT;
    private Direction upRight = Direction.UP_RIGHT;
    private Direction downLeft = Direction.DOWN_LEFT;
    private Direction downRight = Direction.DOWN_RIGHT;
    @Test
    void next() {
        assertEquals(up.next(origin), new Point(-1, 0));
        assertEquals(down.next(origin), new Point(1, 0));
        assertEquals(left.next(origin), new Point(0, -1));
        assertEquals(right.next(origin), new Point(0, 1));
        assertEquals(upLeft.next(origin), new Point(-1, -1));
        assertEquals(upRight.next(origin), new Point(-1, 1));
        assertEquals(downLeft.next(origin), new Point(1, -1));
        assertEquals(downRight.next(origin), new Point(1, 1));


    }
}