package logic.ai;

import logic.gameObjects.Board;
import logic.gameObjects.Player;

public class Evaluator {
    public static int evaluate(Board board, Player player) {
        return ScoreCornerWeightEval.getInstance(board.getSize()).evaluate(board, player);
    }

}
