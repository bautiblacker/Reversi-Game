package model;

import utils.AI;
import utils.Point;

import java.util.*;

public class ReversiModel implements ReversiManager {
    private Player[][] board;
    private Player turn = Player.BLACK;
    private Stack<ReversiData> undoStack = new Stack<>();
    private Map<Point, Set<Point>> possibleMoves;
    private AI aiOptions;
    private int boardSize;

    public ReversiModel(int boardSize, AI ai) {
        this.boardSize = boardSize;
        board = new Player[boardSize][boardSize];
        initBoard();
    }
    public ReversiModel(ReversiModel old, AI newAi) {
        this.board = old.board;
        this.turn = old.turn;
        this.undoStack = old.undoStack;
        this.possibleMoves = old.possibleMoves;
        this.aiOptions = newAi;
    }

    @Override
    public boolean move(Point coordinates) {
        if(getPossibleMoves().isEmpty())
            throw new IllegalArgumentException(); //TODO crear una exception linda.
        if(!isValidMove(coordinates)) {
            return false;
        }

        board[coordinates.getX()][coordinates.getY()] = turn;
        flip(possibleMoves.get(coordinates));
        undoStack.push(new ReversiData(coordinates, possibleMoves.get(coordinates)));
        turn = turn.opposite();
        updatePossibleMoves();
        return true;
    }

    @Override
    public boolean undo() {
        if(undoStack.isEmpty())
            return false;
        ReversiData aux = undoStack.pop();
        board[aux.getPlaced().getX()][aux.getPlaced().getY()] = Player.NONE;
        flip(aux.getFlipped());
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

    private boolean isValidMove(Point coordinates) {
        return possibleMoves.containsKey(coordinates);
    }

    private void initBoard() {
        for(int i=0; i<boardSize; i++)
            Arrays.fill(board[i], Player.NONE);
        int center1 = boardSize/2 -1;
        int center2 = boardSize/2;
        board[center1][center1] = Player.BLACK;
        board[center1][center2] = Player.WHITE;
        board[center2][center1] = Player.WHITE;
        board[center2][center2] = Player.BLACK;
        possibleMoves = new HashMap<>();
        updatePossibleMoves();
    }

    private void updatePossibleMoves() {
        possibleMoves.clear();
        for(int i=0; i<boardSize; i++)
            for(int j=0; j<boardSize; j++)
                if(board[i][j].equals(Player.NONE)) {
                    Point currentCoordinate = new Point(i, j);
                    evaluateMove(currentCoordinate);
                }
    }

    private void evaluateMove(Point coordinate) {
        findLineRec(coordinate, coordinate, 1, 0);
        findLineRec(coordinate, coordinate, 1, 1);
        findLineRec(coordinate, coordinate, 0, 1);
        findLineRec(coordinate, coordinate, -1, 0);
        findLineRec(coordinate, coordinate, -1, 1);
        findLineRec(coordinate, coordinate, 0, -1);
        findLineRec(coordinate, coordinate, -1, -1);
        findLineRec(coordinate, coordinate, 1, -1);
    }

    //FIXME
    //wrong x and y directions.
    private void findLineRec(Point original, Point coordinate, int dx, int dy) {
        if( (coordinate.getX() + dx >= boardSize || coordinate.getX() + dx < 0) ||
                (coordinate.getY() + dy >= boardSize || coordinate.getY() + dy < 0) ||
                board[coordinate.getX() + dx][coordinate.getY() + dy].equals(Player.NONE)) {
            return;
        }
        if(board[coordinate.getX() + dx][coordinate.getY() + dy].equals(turn.opposite()))
            findLineRec(original, new Point(coordinate.getX() + dx, coordinate.getY() + dy), dx, dy);

        if(!possibleMoves.containsKey(original))
            possibleMoves.put(original, new HashSet<>());
        possibleMoves.get(original).add(coordinate);
    }

    private void flip(Collection<Point> points) {
        for(Point point : points) {
            board[point.getX()][point.getY()] = turn.opposite();
        }
    }
}
