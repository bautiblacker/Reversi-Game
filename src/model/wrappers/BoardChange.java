package model.wrappers;

import model.Board;
import model.Point;
import model.ai.Dot;

import java.util.Collection;

public class BoardChange {
    private Board board;
    private Point place;
    private Collection<Point> flip;

    public BoardChange(Board board, Point place, Collection<Point> flip, Dot dot) {
        this.board = board;
        this.place = place;
        this.flip = flip;
    }

    public Board getBoard() {
        return board;
    }

    public Point getPlace() {
        return place;
    }

    public Collection<Point> getFlip() {
        return flip;
    }
}
