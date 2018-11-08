package logic;

import logic.gameObjects.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player white = Player.WHITE;
    private Player black = Player.BLACK;
    private Player none = Player.NONE;
    @Test
    void opposite() {
        assertSame(white, black.opposite());
        assertSame(black, white.opposite());
        assertSame(none, none.opposite());
    }
}