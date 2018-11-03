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
        possibleMoves = new HashMap<>();
        board = new Board(boardSize);
        aiOptions = ai;
        updatePossibleMoves();
    }
    public ReversiGame(ReversiGame old, AI newAi) {
        this.board = old.board;
        this.turn = old.turn;
        this.undoStack = old.undoStack;
        this.possibleMoves = old.possibleMoves;
        this.aiOptions = newAi;
    }

    @Override
    public Collection<Point> move(Point point) {
        if(possibleMoves.keySet().isEmpty())
            throw new IllegalArgumentException(); //TODO crear una exception linda.
        if(!isValidMove(point)) {
            return null;
        }
        Collection<Point> toReturn = possibleMoves.get(point);
        board.flip(toReturn);
        board.setPlayer(point, turn);
        undoStack.push(new ReversiData(point,toReturn));
        turn = turn.opposite();
        updatePossibleMoves();
        return toReturn;
    }

    @Override
    public ReversiData undo() {
        if(undoStack.isEmpty())
            return null;
        ReversiData aux = undoStack.pop();
        board.setPlayer(aux.getPlaced(), Player.NONE);
        board.flip(aux.getFlipped());
        turn = turn.opposite();
        updatePossibleMoves();
        return aux;
    }

    @Override
    public boolean pass() {
        return false;
    }

    @Override
    public int getScore(Player current) {
        return board.getCount(current);
    }

    @Override
    public Collection<Point> getPossibleMoves() {
        return possibleMoves.keySet();
    }


    private Collection<Point> getFlipped() {
        if(undoStack.isEmpty())
            return new Stack<>();
        return undoStack.peek().getFlipped();
    }
    @Override
    public Player getPlayer(Point point) {
        return board.getPlayer(point);
    }

    @Override
    public Player getTurn() {
        return turn;
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
            Point next = dir.next(point);
            if(board.isValidPosition(next) && board.getPlayer(next) != Player.NONE)
                findLineRec(point, next, dir);
        }
    }

    private boolean findLineRec(Point original, Point point, Direction dir) {
        Point next = dir.next(point);
        if(board.getPlayer(point) == turn)
            return true;
        if(!board.isValidPosition(point) || board.getPlayer(point) == Player.NONE)
            return false;
        if(findLineRec(original, dir.next(point), dir)) {
            if (!possibleMoves.containsKey(original))
                possibleMoves.put(original, new HashSet<>());
            possibleMoves.get(original).add(point);
            return true;
        }
        return false;
    }
}
