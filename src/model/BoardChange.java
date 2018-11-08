package model;

import utils.Point;

import java.util.Collection;

public class BoardChange {
    private Board board;
    private Point place; //Point to place
    private Collection<Point> flip; //Points to flip

    public BoardChange(Board board, Point place, Collection<Point> flip) {
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
