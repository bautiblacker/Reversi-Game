package model;

import java.io.Serializable;

public enum Player implements Serializable {

    NONE,
    WHITE,
    BLACK;

    public Player opposite() {
        return this == WHITE ? BLACK : this == BLACK ? WHITE : NONE;
    }
}