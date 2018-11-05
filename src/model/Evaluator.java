package model;

public class Evaluator {
    public static int evaluate(Board board, Player player) {
        return board.getCount(player);
    }
}
