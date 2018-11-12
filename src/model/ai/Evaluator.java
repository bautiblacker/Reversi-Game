package model.ai;

import model.Board;
import model.Player;
import model.ai.heuristics.ScoreCornerWeightEval;

public class Evaluator {
    public static int evaluate(Board board, Player player) {
        return ScoreCornerWeightEval.getInstance(board.getSize()).evaluate(board, player);
    }

}
