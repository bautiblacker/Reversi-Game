package model;

import utils.AI;
import utils.Direction;
import utils.Point;
import utils.Tree;

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
        if(possibleMoves.keySet().isEmpty())
            throw new IllegalArgumentException(); //TODO crear una exception linda.
        if(!isValidMove(point)) {
            return false;
        }

        board.flip(possibleMoves.get(point));
        board.setPlayer(point, turn);
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
    public void playsCPU() {
        /* TODO: este es el min y max, hay que implementar el min y max para cada tipo de parametro, si es por depth o por tiempo y eso*/
    }

    private Tree buildTree() { // TODO: check este metodo, lo hice medio quemado.
        Board b = board;
        Tree t = new Tree(b);
        for(Point point : possibleMoves.keySet()) {
            Board b2 = b; // asi b no cambia;
            if(b2.flip(possibleMoves.get(point)))
                t.add(b2);
        }
        return t;
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
            findLineRec(point, dir.next(point), dir);
        }
    }

    private void findLineRec(Point original, Point point, Direction dir) {
        if(isInvalidBoardPos(point, dir))
            return;

        if(board.getPlayer(dir.next(point)) == (turn.opposite()))
            findLineRec(original, dir.next(point), dir);
        if(!possibleMoves.containsKey(original))
            possibleMoves.put(original, new HashSet<>());
        possibleMoves.get(original).add(point);
    }

    private boolean isInvalidBoardPos(Point point, Direction dir) {
        return dir.next(point).getX() >= board.getSize() || dir.next(point).getX() < 0 ||
                dir.next(point).getY() >= board.getSize() || dir.next(point).getY() < 0 ||
                board.getPlayer(dir.next(point)) == (Player.NONE);
    }
}
