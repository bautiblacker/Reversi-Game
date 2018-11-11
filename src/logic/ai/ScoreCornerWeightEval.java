package logic.ai;

import logic.gameObjects.Board;
import logic.gameObjects.Player;
import utils.Direction;
import utils.Point;

import java.util.*;

public class ScoreCornerWeightEval {
    private static ScoreCornerWeightEval instance;
    private Map<Point, Integer> pointsValue;
    private static final int WEIGHT = 90;
    private int boardSize;


    public static ScoreCornerWeightEval getInstance(int boardSize){
        if(instance == null){
            instance = new ScoreCornerWeightEval(boardSize);
        }
        return instance;
    }

    private ScoreCornerWeightEval(int boardSize) {
        pointsValue = new HashMap<>();
        this.boardSize = boardSize;
        List<Point> corners = Arrays.asList(new Point(0, 0), new Point(0, boardSize-1),
                new Point(boardSize-1, 0), new Point(boardSize-1, boardSize-1));
        int center1 = boardSize/2 -1;
        int center2 = boardSize/2;
        Collection<Point> centers = Arrays.asList(new Point(center1, center1), new Point(center1, center2),
                new Point(center2, center1), new Point(center2, center2));
        for (Point center : centers) {
            pointsValue.put(center, 0);
        }
        for(Point corner : corners) {
            pointsValue.putIfAbsent(corner, WEIGHT);
            for(Direction dir : Direction.values()) {
                if(isValidPoint(dir.next(corner), boardSize))
                    pointsValue.putIfAbsent(dir.next(corner), -WEIGHT);
            }
        }
        Point point = new Point(0, 0);
        Direction dir = Direction.RIGHT;
        setLine(dir.next(dir.next(point)), new Point(0, boardSize -1), dir, WEIGHT/10);
         dir = Direction.DOWN;
        setLine(dir.next(dir.next(point)), new Point(boardSize-1, 0), dir, WEIGHT/10);
         point = new Point(boardSize-1, boardSize-1);
        dir = Direction.LEFT;
        setLine(dir.next(dir.next(point)), new Point(boardSize-1, 0), dir, WEIGHT/10);
        dir = Direction.UP;
        setLine(dir.next(dir.next(point)), new Point(0, boardSize-1), dir, WEIGHT/10);

    }
    private void setLine(Point start, Point end, Direction dir, int value) {
        while(!dir.next(start).equals(end)){
            pointsValue.putIfAbsent(start, value);
            start = dir.next(start);
        }

    }
    private boolean isValidPoint(Point point, int size) {
        return point.getX() >=0 && point.getX() < size && point.getY() >=0 && point.getY() < size;
    }


    public int evaluate(Board board, Player player) {
        int score = board.getCount(player) - board.getCount(player.opposite());
        for(Point vip : pointsValue.keySet()) {
            if(board.getPlayer(vip) == player)
                score +=  pointsValue.get(vip);
        }
        return score;
    }

}
