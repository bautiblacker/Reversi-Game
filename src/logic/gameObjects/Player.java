package logic.gameObjects;

public enum Player {

    NONE,
    WHITE,
    BLACK;

    public Player opposite() {
        return this == WHITE ? BLACK : this == BLACK ? WHITE : NONE;
    }

    public String getColor() {
        return this == Player.WHITE ? "WHITE" : "BLACK";
    }
}