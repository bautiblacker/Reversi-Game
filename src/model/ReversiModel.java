package model;

import utils.Point;

import java.util.Arrays;
import java.util.Stack;

public class ReversiModel implements ReversiManager {
    Player[][] board;
    Player turn;
    Stack<ReversiData> undo = new Stack<>();
    @Override
    public boolean move(Point coordinates) {
        return false;
    }

    @Override
    public boolean undo() {
        return false;
    }

    @Override
    public boolean pass() {
        return false;
    }

    @Override
    public int getScore(Player current) {
        return 0;
    }
}
