package model;

import utils.Point;

import java.io.Serializable;
import java.util.Collection;

public interface ReversiManager extends Serializable {
    boolean move(Point coordinates); //Places chip and returns flipped.
    ReversiData undo();
    boolean pass();
    Player getPlayer(Point point);
    Player getTurn();
    GameState getState();
    Collection<Point> getPossibleMoves();
    int getScore(Player current);
}
