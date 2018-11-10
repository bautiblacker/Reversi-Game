package logic;

import logic.ai.BoardChange;
import logic.ai.BoardChangeData;
import logic.ai.Dot;
import logic.ai.Evaluator;
import logic.gameObjects.Board;
import logic.gameObjects.GameState;
import logic.gameObjects.Player;
import logic.utils.Searcher;
import utils.AI;
import utils.Point;

import java.util.*;

public class ReversiGame implements ReversiManager {
    private Board board;
    private Player turn;
    private Stack<ReversiData> undoStack;
    private Map<Point, Collection<Point>> possibleMovesMap;
    private AI aiOptions;

    public ReversiGame(int boardSize, AI ai) {
        turn = Player.BLACK;
        undoStack = new Stack<>();
        board = new Board(boardSize);
        aiOptions = ai;
        possibleMovesMap = Searcher.findPossibleMoves(this.board, this.turn);
    }
    public ReversiGame(ReversiGame old, AI newAi) {
        this.board = old.board;
        this.turn = old.turn;
        this.undoStack = old.undoStack;
        this.possibleMovesMap = old.possibleMovesMap;
        this.aiOptions = newAi;
    }

    private void move(Point point) {
        Collection<Point> toFlip = possibleMovesMap.get(point);
        board = makeBoardMove(board, turn, point, toFlip);
        undoStack.push(new ReversiData(point,toFlip));
        turn = turn.opposite();
        possibleMovesMap = Searcher.findPossibleMoves(board, turn);
    }
    @Override
    public boolean movePlayer(Point point){
        if(!isValidMove(point)) {
            return false;
        }
        move(point);
        return true;
    }

    @Override
    public Dot moveCPU() {
        BoardChangeData minimax = minimax();
        if(minimax == null)
            return null;
        move(minimax.getBoardChange().getPlace());
        return minimax.getDot();
    }

    @Override
    public ReversiData undo() {
        if(undoStack.isEmpty())
            return null;
        ReversiData aux = undoStack.pop();
        board = undoBoardMove(board, turn, aux.getPlaced(), aux.getFlipped());
        turn = turn.opposite();
        possibleMovesMap = Searcher.findPossibleMoves(board, turn);
        return aux;
    }

    @Override
    public boolean pass() {
        if(getState() == GameState.OUT_OF_MOVES){
            turn = turn.opposite();
            possibleMovesMap = Searcher.findPossibleMoves(board, turn);
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
        return possibleMovesMap.keySet();
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
        if(getPossibleMoves().isEmpty()) {
            if(otherPlayerCanMove())
                return GameState.OUT_OF_MOVES;
            return GameState.GAME_OVER;
        }
        return GameState.RUNNING;
    }


    private Collection<BoardChange> getPossibleBoardChanges(Board board, Player player) {
        Collection<BoardChange> boardChanges = new HashSet<>();
        Searcher.findPossibleMoves(board, player).forEach((point, points) -> {
            Dot dot = new Dot(point, 0);
            boardChanges.add(new BoardChange(board, point, points, dot));
        });
        return boardChanges;
    }

    private BoardChangeData minimax() {
        Dot.resetCounter();
        Dot dot = new Dot(null, 0);
        BoardChange toEval = new BoardChange(board, null, null, dot);
        if(aiOptions.getType().equals("depth")) {
            toEval = minimaxD(toEval, turn, aiOptions.getParam(),
                    Integer.MIN_VALUE, Integer.MAX_VALUE, true, aiOptions.isPrune(), dot);
            System.out.println(Dot.tree(dot));
            return new BoardChangeData(toEval, dot);
        }
//        if(aiOptions.getType().equals("time")){
//            //Not working correctly
//            long start = System.currentTimeMillis();
//            long timeLimit = start + aiOptions.getParam()*1000;
//            int depth = 1;
//            BoardChange timeBoard = minimaxD(toEval, turn, depth,
//                    Integer.MIN_VALUE, Integer.MAX_VALUE, true, aiOptions.isPrune() );
//
//            while(System.currentTimeMillis() < timeLimit ) {
//                timeBoard = minimaxD(new BoardChange(board, null, null), turn, depth++,
//                        Integer.MIN_VALUE, Integer.MAX_VALUE, true, aiOptions.isPrune());
//
//            }
//            return timeBoard;
//        }
        return null;
    }

    private BoardChange minimaxD(BoardChange boardChange, Player player, int depth, int alpha, int beta, boolean isMax,
                           boolean isPrune, Dot dot) {
        Collection<BoardChange> possibles = getPossibleBoardChanges(boardChange.getBoard(), player);
        if(depth == 0 || possibles.size() == 0) {
            return boardChange;
        }
        if(isMax) {
            int value = Integer.MIN_VALUE;
            BoardChange maxBoardChange = null;
            Dot maxDot = null;
            boolean wasPruned = false;
            for(BoardChange possible : possibles) {
                Dot auxDot = new Dot(possible.getPlace(), 0);
                if(!wasPruned) {
                    makeBoardMove(possible.getBoard(), player, possible.getPlace(), possible.getFlip());
                    BoardChange nextBoardChange = minimaxD(possible, player.opposite(), depth - 1, alpha, beta, false,
                            isPrune, auxDot);
                    Board nextBoard = nextBoardChange.getBoard();
                    int auxValue = Evaluator.evaluate(nextBoard, player.opposite());
                    auxDot.setValue(auxValue);
                    if (auxValue > value) {
                        maxBoardChange = possible;
                        maxDot = auxDot;
                        value = auxValue;
                    }
                    undoBoardMove(nextBoard, player.opposite(), possible.getPlace(), possible.getFlip());
                    if (isPrune) {
                        alpha = Math.max(alpha, value);
                        if (beta <= alpha) {
                            wasPruned = true; //prunes subtree
                        }
                    }
                }
                else {
                    auxDot.setPruned(true);
                    dot.getNeighbours().add(auxDot);
                }
                dot.getNeighbours().add(auxDot);
            }
            if(maxDot != null)
                maxDot.setChosen(true);

            return maxBoardChange;
        }
        else {
            int value = Integer.MAX_VALUE;
            BoardChange minBoardChange = null;
            Dot minDot = null;
            boolean wasPruned = false;
            for(BoardChange possible : possibles) {
                Dot auxDot = new Dot(possible.getPlace(), 0);
                if(!wasPruned) {
                    makeBoardMove(possible.getBoard(), player, possible.getPlace(), possible.getFlip());
                    BoardChange nextBoardChange = minimaxD(possible, player.opposite(), depth - 1, alpha, beta, true, isPrune, auxDot);
                    Board nextBoard = nextBoardChange.getBoard();
                    int auxValue = Evaluator.evaluate(nextBoard, player.opposite());
                    auxDot.setValue(auxValue);
                    if (auxValue < value) {
                        minBoardChange = possible;
                        value = auxValue;
                        minDot = auxDot;

                    }
                    undoBoardMove(nextBoard, player.opposite(), possible.getPlace(), possible.getFlip());
                    if (isPrune) {
                        beta = Math.min(beta, value);
                        if (beta <= alpha) {
                            wasPruned = true; //prunes subtree
                        }
                    }
                }
                else {
                    auxDot.setPruned(true);
                    dot.getNeighbours().add(auxDot);
                }
                dot.getNeighbours().add(auxDot);
            }
            if(minDot != null)
                minDot.setChosen(true);

            return minBoardChange;
        }
    }

    private Board makeBoardMove(Board board, Player player, Point toPlace, Collection<Point> toFlip ) {
        board.setPlayer(toPlace, player);
        board.flip(toFlip);
        return board;
    }
    private Board undoBoardMove(Board board, Player player, Point toRemove, Collection<Point> toFlip) {
        board.setPlayer(toRemove, Player.NONE);
        board.flip(toFlip);
        return board;
    }
    private boolean isValidMove(Point coordinates) {
        return possibleMovesMap.containsKey(coordinates);
    }

    private boolean otherPlayerCanMove() {
        return !Searcher.findPossibleMoves(board, turn.opposite()).isEmpty();
    }
}
