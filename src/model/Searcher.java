package model;

import utils.Direction;
import utils.Point;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Searcher {
    public static Map<Point, Collection<Point>> findPossibleMoves(Board board, Player player) {
        Map<Point, Collection<Point>> toReturn = new HashMap<>();
        for(Point point : board.findMatchingPoints(Player.NONE)) {
            Collection<Point> toFlip = evaluateMove(board, point, player);
            if(!toFlip.isEmpty())
                toReturn.put(point, toFlip);
        }
        return toReturn;
    }

    private static Collection<Point> evaluateMove(Board board, Point point, Player player) {
        Collection<Point> toFlip = new HashSet<>();
        for (Direction dir : Direction.values()) {
            Point next = dir.next(point);
            if(board.isValidPosition(next) && board.getPlayer(next) != Player.NONE)
                findLineRec(board, next, dir, player, toFlip);
        }
        return toFlip;
    }

    private static boolean findLineRec(Board board, Point point, Direction dir, Player player, Collection<Point> toFlip) {
        if(board.getPlayer(point) == player)
            return true;
        if(!board.isValidPosition(point) || board.getPlayer(point) == Player.NONE)
            return false;
        if(findLineRec(board, dir.next(point), dir, player, toFlip)) {
            toFlip.add(point);
            return true;
        }
        return false;
    }
}
