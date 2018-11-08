package logic.ai;

import logic.gameObjects.Board;
import logic.gameObjects.Player;
import utils.Direction;
import utils.Point;

import java.util.HashMap;
import java.util.Map;

public class ScoreCornerWeightEval {
    private static ScoreCornerWeightEval instance;
    private Map<Point, Integer> pointsValue;
    private int weight;
    private int boardSize;

    private int[][] matrix;

    public static ScoreCornerWeightEval getInstance(int boardSize){
        if(instance == null){
            instance = new ScoreCornerWeightEval(boardSize);
        }
        return instance;
    }

    private ScoreCornerWeightEval(int boardSize) {
        this.boardSize = boardSize;
        pointsValue = new HashMap<>();
        matrix = new int[boardSize][boardSize];
        initiateMap();
    }

    private void initiateMap() {
        int maxValue = (int) Math.pow((boardSize/2)%2, 2);
        setMatrix(maxValue, boardSize/2);
        setMatrix(maxValue, boardSize/2);
        setQuarters(0, boardSize - 1);
        setQuarters(boardSize-1, 0);
        setQuarters(boardSize-1, boardSize - 1);

    }
    @SuppressWarnings("Duplicates")
    private void initMap() {
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                pointsValue.put(new Point(x, y), 0);
            }
        }
        int center1 = boardSize / 2 - 1;
        int center2 = boardSize / 2;
        pointsValue.put(new Point(center1, center1), 1);
        pointsValue.put(new Point(center2, center1), 1);
        pointsValue.put(new Point(center1, center2), 1);
        pointsValue.put(new Point(center2, center2), 1);
    }

    private void setMatrix(int value, int size){
        int aux = 0;
        matrix[0][0] = value;
        for(int i = 0; i < size; i++) {
            aux = value;
            for(int j = 0; j < size; j++){
                if(i%2 == 0 && j%2 == 0){
                    matrix[i][j] = aux;
                    pointsValue.put(new Point(i,j), value);
                }
                for(Direction dir : Direction.values()) {
                    Point point = dir.next(new Point(i,j));
                    if(isValid(point, size) && Math.abs(matrix[point.getX()][point.getY()]) < Math.abs(aux)){
                        pointsValue.put(point, -value);
                        matrix[point.getX()][point.getY()] = -aux;
                    }
                }
                j++;
                aux = aux - 1;
            }
            i++;
            value--;
        }
    }

    private void setQuarters(int sizeX, int sizeY) {
        for(int i = 0; i < boardSize/2; i ++) {
            int aux = sizeY;
            for(int j = 0; j < boardSize/2; j ++) {
                if(i + sizeX < boardSize && j + aux < boardSize) {
                    pointsValue.put(new Point(i + sizeX, j + aux), matrix[i][j]);
                }
                if(sizeY != 0) {
                    aux-=2;
                }
            }
            if(sizeX != 0) {
                sizeX-=2;
            }
        }
    }

    private boolean isValid(Point point, int size){
        return point.getX() >= 0 && point.getX() < size && point.getY() >= 0 && point.getY() < size;
    }

    public int evaluate(Board board, Player player) {
        final int[] score = {board.getCount(player)};
        pointsValue.forEach((k,v) -> {
            if(board.getPlayer(k) == player)
                score[0] += v;
        });

        return score[0];
    }

}
