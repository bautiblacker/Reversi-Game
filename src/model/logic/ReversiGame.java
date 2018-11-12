package model.logic;

import model.*;
import model.ai.Dot;
import model.ai.Evaluator;
import model.utils.Searcher;
import model.utils.TimeLimit;
import model.wrappers.AI;
import model.wrappers.BoardChange;
import model.wrappers.BoardChangeData;
import model.wrappers.ReversiData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

public class ReversiGame implements ReversiManager {
    //Depth limit to avoid using too much memory and lagging the game while running against ai in time mode.
    public static final int DEPTH_HARD_LIMIT = 8;
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
    public int getBoardSize() {
        return board.getSize();
    }
    @Override
    public ReversiData undo() {
        if(undoStack.isEmpty())
            return null;
        ReversiData aux = undoStack.pop();
        board = undoBoardMove(board, aux.getPlaced(), aux.getFlipped());
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

    private void move(Point point) {
        Collection<Point> toFlip = possibleMovesMap.get(point);
        board = makeBoardMove(board, turn, point, toFlip);
        undoStack.push(new ReversiData(point,toFlip));
        turn = turn.opposite();
        possibleMovesMap = Searcher.findPossibleMoves(board, turn);
    }

    private BoardChangeData minimax() {
        Dot.resetCounter();
        Dot dot = new Dot(null, 0);
        BoardChange depthBoard = new BoardChange(board, null, null, dot);
        if(aiOptions.getType().equals("depth")) {
            int depth = aiOptions.getParam() > DEPTH_HARD_LIMIT ? DEPTH_HARD_LIMIT : aiOptions.getParam();
            depthBoard = minimaxD(depthBoard, turn, depth,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, true, aiOptions.isPrune(), dot);
            return new BoardChangeData(depthBoard, dot);
        }
        if(aiOptions.getType().equals("time")){
            TimeLimit timeLimit = new TimeLimit(aiOptions.getParam()*1000);
            int depth = 1;
            BoardChange timeBoard = minimaxT(depthBoard, turn, depth,
                    Integer.MIN_VALUE, Integer.MAX_VALUE, true, aiOptions.isPrune(), dot, timeLimit );

            while(!timeLimit.isExceeded() && depth <= DEPTH_HARD_LIMIT) {

                BoardChange auxBoard = minimaxT(new BoardChange(board, null, null, dot), turn, depth++,
                        Integer.MIN_VALUE, Integer.MAX_VALUE, true, aiOptions.isPrune(), dot, timeLimit);
                if(!timeLimit.isAborted())
                    timeBoard = auxBoard;

                //Gives time for the stack to clear from the recursive function.
                //Without this the game hangs on certain computers.
                try {
                    Thread.sleep(700);
                }catch (InterruptedException ex){
                    ex.printStackTrace();
                }
            }
            return new BoardChangeData(timeBoard, dot);
        }
        return null;
    }

    private Collection<BoardChange> getPossibleBoardChanges(Board board, Player player) {
        Collection<BoardChange> boardChanges = new HashSet<>();
        Searcher.findPossibleMoves(board, player).forEach((point, points) -> {
            Dot dot = new Dot(point, 0);
            boardChanges.add(new BoardChange(board, point, points, dot));
        });
        return boardChanges;
    }

    @SuppressWarnings("Duplicates")
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
                    int auxValue = Evaluator.evaluate(nextBoard, player);
                    auxDot.setValue(auxValue);
                    if (auxValue > value) {
                        maxBoardChange = possible;
                        maxDot = auxDot;
                        value = auxValue;
                    }
                    undoBoardMove(nextBoard, possible.getPlace(), possible.getFlip());
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
                    int auxValue = Evaluator.evaluate(nextBoard, player);
                    auxDot.setValue(auxValue);
                    if (auxValue < value) {
                        minBoardChange = possible;
                        value = auxValue;
                        minDot = auxDot;

                    }
                    undoBoardMove(nextBoard, possible.getPlace(), possible.getFlip());
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
    @SuppressWarnings("Duplicates")
    private BoardChange minimaxT(BoardChange boardChange, Player player, int depth, int alpha, int beta, boolean isMax,
                                 boolean isPrune, Dot dot, TimeLimit timeLimit) {
        Collection<BoardChange> possibles = getPossibleBoardChanges(boardChange.getBoard(), player);
        if(depth == 0 ||  timeLimit.isExceeded()|| possibles.size() == 0) {
            timeLimit.setAborted(true);
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
                    int auxValue = Evaluator.evaluate(nextBoard, player);
                    auxDot.setValue(auxValue);
                    if (auxValue > value) {
                        maxBoardChange = possible;
                        maxDot = auxDot;
                        value = auxValue;
                    }
                    undoBoardMove(nextBoard, possible.getPlace(), possible.getFlip());
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
                    int auxValue = Evaluator.evaluate(nextBoard, player);
                    auxDot.setValue(auxValue);
                    if (auxValue < value) {
                        minBoardChange = possible;
                        value = auxValue;
                        minDot = auxDot;

                    }
                    undoBoardMove(nextBoard, possible.getPlace(), possible.getFlip());
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
    private Board undoBoardMove(Board board, Point toRemove, Collection<Point> toFlip) {
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
