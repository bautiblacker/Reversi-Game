package model;

import utils.AI;
import utils.Direction;
import utils.Point;

import java.util.*;
import java.util.stream.Collectors;

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
        if(movePlayer(point)) {
            if(aiOptions.getRole() != 0)
                moveCPU();
            return true;
        }
        return false;
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
            updatePossibleMoves();
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
    private boolean movePlayer(Point point){
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
    private void moveCPU() {
        movePlayer(obtainPosition(minimax()));
    }
    private Point obtainPosition(Board board) {
        for (Point point : this.board.findMatchingPoints(Player.NONE)){
            if(board.getPlayer(point) != Player.NONE)
                return point;
        }
        return null;
    }
    private Collection<Board> getPossibleBoards(Board board, Player player) {
        Collection<Map<Point, Collection<Point>>> moves = new HashSet<>();
        for(Point point :board.findMatchingPoints(Player.NONE)) {
            Map<Point, Collection<Point>> aux = new HashMap<>();
            evaluateMove(point, player, aux);
            moves.add(aux);
        }
        return moves.stream()
                .map(x -> getBoard(board, player, x))
                .collect(Collectors.toCollection(HashSet::new));
    }
    private Board getBoard(Board board, Player player, Map<Point, Collection<Point>> moves) {
        Board toReturn = board.getBoardCopy();
        moves.forEach((point, points) -> {
            toReturn.setPlayer(point, player);
            toReturn.flip(points);
        });
        return toReturn;
    }
    private Board minimax() {
        if(aiOptions.getType().equals("depth"))
           return minimaxD(board, turn, aiOptions.getParam(), Integer.MIN_VALUE, Integer.MAX_VALUE, true,
                    aiOptions.isPrune() );
        if(aiOptions.getType().equals("time"))
            return null; //TODO implement minimaxT
        return null;
    }
    private Board minimaxD(Board board, Player player, int depth, int alpha, int beta, boolean isMax,
                           boolean isPrune) {
        Collection<Board> possibles = getPossibleBoards(board, player);
        if(depth == 0 || possibles.size() == 0)
            return board;
        if(isMax) {
            int value = Integer.MIN_VALUE;
            Board maxBoard = null;
            for(Board boardP : possibles) {
                Board nextBoard = minimaxD(boardP, player.opposite(), depth -1, alpha, beta, false,
                        isPrune);
                if(Evaluator.evaluate(nextBoard, player.opposite()) > value) {
                    maxBoard = boardP;
                    value = Evaluator.evaluate(nextBoard, player);
                }
                if(isPrune) {
                    alpha = Math.max(alpha, value);
                    if (beta <= alpha) {
                        break; //prunes subtree
                    }
                }
            }
            return maxBoard;
        }
        else {
            int value = Integer.MAX_VALUE;
            Board minBoard = null;
            for(Board boardP : possibles) {
                Board nextBoard = minimaxD(boardP, player.opposite(), depth-1, alpha, beta, true, isPrune);
                if(Evaluator.evaluate(nextBoard, player.opposite()) < value) {
                    minBoard = boardP;
                    value = Evaluator.evaluate(nextBoard, player);
                }
                if(isPrune) {
                    beta = Math.min(beta, value);
                    if(beta <= alpha) {
                        break; //prunes subtree
                    }
                }
            }
            return minBoard;
        }
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
