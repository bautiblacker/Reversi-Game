package logic;

import utils.Point;

import java.io.Serializable;
import java.util.Collection;

public class ReversiData implements Serializable {
    private Point placed;
    private Collection<Point> flipped;

    public ReversiData(Point placed, Collection<Point> flipped) {
        this.placed = placed;
        this.flipped = flipped;
    }

    public Point getPlaced() {
        return placed;
    }

    public Collection<Point> getFlipped() {
        return flipped;
    }
}