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
    public boolean isValidPosition(Point point) {
        return board.containsKey(point);
    }
}
