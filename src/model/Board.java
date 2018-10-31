package model;

import utils.Direction;
import utils.Point;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Board {
    private int boardSize;
    private Map<Point, Player> board;
    private Map<Point, Collection<Point>> possibleMoves;

    public Board(int boardSize) {
        this.boardSize = boardSize;
        board = new HashMap<>(boardSize * boardSize);
        initBoard();
    }

    public Player getPlayer(Point point) {
        return board.get(point);
    }

    public void setPlayer(Point point, Player player) {
        board.put(point, player);
    }

    public int getCount(Player player) {
        return (int) board.values()
                .stream()
                .filter(player1 -> player1.equals(player))
                .count();
    }
    public int getSize() {
        return boardSize;
    }

    public boolean flip(Collection<Point> points) {
        for(Point point : points) {
            if(!board.containsKey(point))
                return false;
            board.computeIfPresent(point, (pos, player) -> player.opposite());
        }
        return true;
    }

    private void initBoard() {
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                board.put(new Point(x, y), Player.NONE);
            }
        }
        int center1 = boardSize / 2 - 1;
        int center2 = boardSize / 2;
        board.put(new Point(center1, center1), Player.WHITE);
        board.put(new Point(center2, center1), Player.BLACK);
        board.put(new Point(center1, center2), Player.BLACK);
        board.put(new Point(center2, center2), Player.WHITE);
    }
    public Collection<Point> findMatchingPoints(Player playerToMatch) {
        Collection<Point> toReturn = new HashSet<>();
        board.forEach((point, player) ->{
            if(player.equals(playerToMatch))
                toReturn.add(point);
        });
        return toReturn;
    }
    private void updatePossibleMoves(Player turn) {
        possibleMoves.clear();
        board.forEach((point, player) ->{
            if(player.equals(Player.NONE))
                evaluateMove(point, turn);
        });
    }

    private void evaluateMove(Point point, Player turn) {
        for (Direction dir : Direction.values()) {
            findLineRec(point, point, dir, turn);
        }


    }

    private void findLineRec(Point original, Point point, Direction dir, Player turn) {
        if(dir.next(point).getX() >= boardSize || dir.next(point).getX() < 0 ||
                dir.next(point).getY() >= boardSize || dir.next(point).getY() < 0 ||
                board.get(dir.next(point)).equals(Player.NONE))
            return;

        if(board.get(dir.next(point)).equals(turn.opposite()))
            findLineRec(original, dir.next(point), dir, turn);
        if(!possibleMoves.containsKey(original))
            possibleMoves.put(original, new HashSet<>());
        possibleMoves.get(original).add(point);
    }
}
