package model;

import javafx.scene.paint.Color;

public enum Player {

    NONE,
    WHITE,
    BLACK;

    public Player opposite() {
        return this == WHITE ? BLACK : this == BLACK ? WHITE : NONE;
    }

    public Color getColor() {
        return this == Player.WHITE ? Color.WHITE : Color.BLACK;
    }

    public String getColorStyle() {
        return this == Player.WHITE ? "white" : "black";
    }
}