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
    public boolean move(Point point) {
        if(!isValidMove(point)) {
            return false;
        }
        Collection<Point> toFlip = possibleMoves.get(point);
        board.flip(toFlip);
        board.setPlayer(point, turn);
        undoStack.push(new ReversiData(point,toFlip));
        turn = turn.opposite();
        updatePossibleMoves();
        return true;
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
        if(getState() == GameState.OUT_OF_MOVES){
            turn = turn.opposite();
            return true;
        }
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

    @Override
    public Player getPlayer(Point point) {
        return board.getPlayer(point);
    }

    @Override
    public Player getTurn() {
        return turn;
    }

    @Override
    public GameState getState() {
        if(possibleMoves.keySet().isEmpty()) {
            if(otherPlayerCanMove())
                return GameState.OUT_OF_MOVES;
            return GameState.GAME_OVER;
        }
        return GameState.RUNNING;
    }

    private boolean isValidMove(Point coordinates) {
        return possibleMoves.containsKey(coordinates);
    }

    private void updatePossibleMoves() {
        this.possibleMoves.clear();
        updateMoves(this.board, turn, this.possibleMoves);
    }

    private void updateMoves(Board board, Player player, Map<Point, Collection<Point>> moves) {
        moves.clear();
        for(Point point : board.findMatchingPoints(Player.NONE))
            evaluateMove(point, player, moves);
    }

    private boolean otherPlayerCanMove() {
        Map<Point, Collection<Point>> nextPossibleMoves= new HashMap<>();
        for(Point point : board.findMatchingPoints(Player.NONE)){
            evaluateMove(point, turn.opposite(), nextPossibleMoves);
        }
        return !nextPossibleMoves.isEmpty();
    }

    private void evaluateMove(Point point, Player player, Map<Point, Collection<Point>> moves) {
        for (Direction dir : Direction.values()) {
            Point next = dir.next(point);
            if(board.isValidPosition(next) && board.getPlayer(next) != Player.NONE)
                findLineRec(point, next, dir, player, moves);
        }
    }

    private boolean findLineRec(Point original, Point point, Direction dir,
                                Player player, Map<Point, Collection<Point>> moves) {
        Point next = dir.next(point);
        if(board.getPlayer(point) == player)
            return true;
        if(!board.isValidPosition(point) || board.getPlayer(point) == Player.NONE)
            return false;
        if(findLineRec(original, dir.next(point), dir,player, moves)) {
            if (!moves.containsKey(original))
                moves.put(original, new HashSet<>());
            moves.get(original).add(point);
            return true;
        }
        return false;
    }
}
