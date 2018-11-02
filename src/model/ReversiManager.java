package model;

import utils.Point;

import java.io.Serializable;
import java.util.Collection;

public interface ReversiManager extends Serializable {
    Collection<Point> move(Point coordinates); //Places chip and returns flipped.
    boolean undo();
    boolean pass();
    Player getPlayer(Point point);
    Player getTurn();
    Collection<Point> getPossibleMoves();
    int getScore(Player current);
}
