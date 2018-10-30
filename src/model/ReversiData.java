package model;

import javafx.beans.property.ObjectProperty;


public class ReversiData {
    private ObjectProperty<Player> turn;
    private ObjectProperty<Player>[][] board;

    public ReversiData(ObjectProperty<Player> turn, ObjectProperty<Player>[][] board) {
        this.turn = turn;
        this.board = board;
    }
}
