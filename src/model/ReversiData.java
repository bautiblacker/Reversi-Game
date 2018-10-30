package model;

public class ReversiData {
    private Player turn;
    private Player[][] board;

    public ReversiData(Player turn, Player[][] board) {
        this.turn = turn;
        this.board = board;
    }

    public Player[][] getBoard(){
        return board;
    }

    public Player getTurn(){
        return turn;
    }
}
