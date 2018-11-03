package model;

import utils.Point;

import java.io.Serializable;
import java.util.Collection;

public interface ReversiManager extends Serializable {
    boolean move(Point coordinates);
    boolean undo();
    boolean pass();
    Player getPlayer(Point point);
    Collection<Point> getPossibleMoves();
    int getScore(Player current); //Could be implemented directly in front-end.
    void playsCPU(); // minMax method
}
