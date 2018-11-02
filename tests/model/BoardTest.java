package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Point;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    @BeforeEach
    void setUp() {
        board =  new Board(8);
    }

    @Test
    void getPlayer() {
        assertEquals(Player.WHITE, board.getPlayer(new Point(3, 3)));
    }

    @Test
    void setPlayer() {
        board.setPlayer(new Point(0,0), Player.BLACK);
        assertEquals(Player.BLACK, board.getPlayer(new Point(0, 0)));
    }

    @Test
    void getCount() {
        assertEquals(2, board.getCount(Player.BLACK));
        assertEquals(2, board.getCount(Player.WHITE));
        assertEquals(60, board.getCount(Player.NONE));
    }

    @Test
    void getSize() {
        assertEquals(8, board.getSize());
    }

    @Test
    void flip() {
        List<Point> actualPoints;
        Point center00 = new Point(3, 3); //white
        Point center01 = new Point(3, 4); //black
        Point center10 = new Point(4, 3); //black
        Point center11 = new Point(4, 4); //white

       actualPoints = Arrays.asList(center00, center01, center10, center11);
       board.flip(actualPoints);
       assertEquals(board.getPlayer(center00), Player.BLACK);
       assertEquals(board.getPlayer(center01), Player.WHITE);
       assertEquals(board.getPlayer(center10), Player.WHITE);
       assertEquals(board.getPlayer(center11), Player.BLACK);
       assertEquals(board.getPlayer(new Point(0, 0)), Player.NONE);
    }

    @Test
    void findMatchingPoints() {
        Point center00 = new Point(3, 3); //white
        Point center01 = new Point(3, 4); //black
        Point center10 = new Point(4, 3); //black
        Point center11 = new Point(4, 4); //white

        Set<Point> whites = new HashSet<>(Arrays.asList(center00, center11));
        Set<Point> blacks = new HashSet<>(Arrays.asList(center01, center10));

        assertEquals(whites, board.findMatchingPoints(Player.WHITE));
        assertEquals(blacks, board.findMatchingPoints(Player.BLACK));

    }

    @Test
    void isValidPosition() {
        Point existing = new Point(3, 3); //white
        Point notExisting = new Point(-1, -1);

        assertTrue(board.isValidPosition(existing));
        assertFalse(board.isValidPosition(notExisting));

    }
}