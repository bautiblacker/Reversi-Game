package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {
    private Point point = new Point(0, 1);
    @Test
    void getX() {
        assertEquals(0, point.getX());
    }

    @Test
    void getY() {
        assertEquals(1, point.getY());
    }

    @Test
    void equals() {
        assertEquals(new Point(0, 1), point);
    }

}