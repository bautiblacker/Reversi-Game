package model.wrappers;

import model.ai.Dot;

public class BoardChangeData {
    private BoardChange boardChange;
    private Dot dot;
    public BoardChangeData(BoardChange boardChange, Dot dot) {
        this.boardChange = boardChange;
        this.dot = dot;
    }
    public BoardChange getBoardChange() { return  boardChange; }
    public Dot getDot() { return dot; }
}
