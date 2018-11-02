package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.AI;
import utils.Point;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
@SuppressWarnings("Duplicates")
class ReversiGameTest {
    private ReversiManager game;

    @Test
    void move() {
        game = new ReversiGame(8, new AI(0, null, 0, false));
        assertTrue(game.move(new Point(2, 3)));
        assertFalse(game.move(new Point(2, 3)));
    }
    @Test
    void colorsFlipCorrectly() {
        game = new ReversiGame(8, new AI(0, null, 0, false));
        game.move(new Point(2, 3));
        Player expected = Player.BLACK;
        Player actual = game.getPlayer(new Point(3, 3));
        assertSame(expected, actual);
    }
    @Test
    void colorsFlipCorrectly2() {
        game = new ReversiGame(4, new AI(0, null, 0, false));
        game.move(new Point(0, 1));
        game.move(new Point(0, 2));
        game.move(new Point(0, 3));
        Player expected = Player.WHITE;
        Player actual1 = game.getPlayer(new Point(0, 2));
        Player actual2 = game.getPlayer(new Point(1, 2));
        assertSame(expected, actual1);
        assertSame(expected, actual2);
    }

    @Test
    void undo() {
    }

    @Test
    void pass() {
    }

    @Test
    void getScore() {
        game = new ReversiGame(8, new AI(0, null, 0, false));
        assertEquals(2, game.getScore(Player.BLACK));
        assertEquals(2, game.getScore(Player.WHITE));
        assertEquals(60, game.getScore(Player.NONE));
    }

    @Test
    void getPossibleMoves() {
        game = new ReversiGame(4, new AI(0, null, 0, false));
        Point expectedPoint1 = new Point(0, 1);
        Point expectedPoint2 = new Point(1, 0);
        Point expectedPoint3 = new Point(2, 3);
        Point expectedPoint4 = new Point(3, 2);
        Collection<Point> expectedPossibleMoves = new HashSet<>(Arrays.asList(expectedPoint1, expectedPoint2,
                expectedPoint3, expectedPoint4));
        assertEquals(expectedPossibleMoves, game.getPossibleMoves());
    }

    @Test
    void getPlayer() {
        game = new ReversiGame(8, new AI(0, null, 0, false));
        Player expected = Player.BLACK;
        Player actual = game.getPlayer(new Point(4, 3));
        assertSame(expected, actual);
    }
}