package model;

import utils.AI;
import utils.Direction;
import utils.Point;

import java.util.*;

public class ReversiGame implements ReversiManager {
    private Board board;
    private Player turn;
    private Stack<ReversiData> undoStack;
    private Map<Point, Collection<Point>> possibleMoves;
    private AI aiOptions;

    public ReversiGame(int boardSize, AI ai) {
        turn = Player.BLACK;
        undoStack = new Stack<>();
        board = new Board(boardSize);
        updatePossibleMoves();
    }
    public ReversiGame(ReversiGame old, AI newAi) {
        this.board = old.board;
        this.turn = old.turn;
        this.undoStack = old.undoStack;
        this.aiOptions = newAi;
    }

    @Override
    public boolean move(Point point) {
        if(possibleMoves.keySet().isEmpty())
            throw new IllegalArgumentException(); //TODO crear una exception linda.
        if(!isValidMove(point)) {
            return false;
        }

        board.setPlayer(point, turn);
        board.flip(possibleMoves.get(point));
        undoStack.push(new ReversiData(point, possibleMoves.get(point)));
        turn = turn.opposite();
        updatePossibleMoves();
        return true;
    }

    @Override
    public boolean undo() {
        if(undoStack.isEmpty())
            return false;
        ReversiData aux = undoStack.pop();
        board.setPlayer(aux.getPlaced(), Player.NONE);
        board.flip(aux.getFlipped());
        turn = turn.opposite();
        return true;
    }

    @Override
    public boolean pass() {
        return false;
    }

    @Override
    public int getScore(Player current) {
        return 0;
    }

    @Override
    public Collection<Point> getPossibleMoves() {
        return possibleMoves.keySet();
    }

    @Override
    public Player getPlayer(Point point) {
        return board.getPlayer(point);
    }

    private boolean isValidMove(Point coordinates) {
        return possibleMoves.containsKey(coordinates);
    }

    private void updatePossibleMoves() {
        possibleMoves.clear();
        for(Point point : board.findMatchingPoints(Player.NONE))
            evaluateMove(point);
    }

    private void evaluateMove(Point point) {
        for (Direction dir : Direction.values()) {
            findLineRec(point, point, dir);
        }


    }

    private void findLineRec(Point original, Point point, Direction dir) {
        if(dir.next(point).getX() >= board.getSize() || dir.next(point).getX() < 0 ||
                dir.next(point).getY() >= board.getSize() || dir.next(point).getY() < 0 ||
                board.getPlayer(dir.next(point)).equals(Player.NONE))
            return;

        if(board.getPlayer(dir.next(point)).equals(turn.opposite()))
            findLineRec(original, dir.next(point), dir);
        if(!possibleMoves.containsKey(original))
            possibleMoves.put(original, new HashSet<>());
        possibleMoves.get(original).add(point);
    }

}
