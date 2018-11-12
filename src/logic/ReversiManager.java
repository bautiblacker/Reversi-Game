package logic;

import logic.ai.Dot;
import logic.gameObjects.GameState;
import logic.gameObjects.Player;
import utils.Point;

import java.io.Serializable;
import java.util.Collection;

public interface ReversiManager extends Serializable {
    boolean movePlayer(Point coordinates);
    Dot moveCPU();
    ReversiData undo();
    boolean pass();
    Player getPlayer(Point point);
    Player getTurn();
    GameState getState();
    Collection<Point> getPossibleMoves();
    int getScore(Player current);
    int getBoardSize();
}
