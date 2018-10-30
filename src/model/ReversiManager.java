package model;

import utils.Point;

public interface ReversiManager {
    boolean move(Point coordinates);
    boolean undo();
    boolean pass();
    int getScore(Player current); //Could be implemented directly in front-end.
}
