package utils;

public enum Direction {
    UP(-1, 0),
    DOWN(+1, 0),
    LEFT(0, -1),
    RIGHT(0, +1),
    UP_LEFT(-1, -1),
    DOWN_RIGHT(+1, +1),
    DOWN_LEFT(+1, -1),
    UP_RIGHT(-1, +1);
    private int rowstep;
    private int colstep;

    private Direction(int rowstep, int colstep) {
        this.rowstep = rowstep;
        this.colstep = colstep;
    }

    public Point next(Point point) {
        return new Point(point.getX() + rowstep, point.getY() + colstep);
    }
}