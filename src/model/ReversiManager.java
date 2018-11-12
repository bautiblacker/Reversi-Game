package model;

import model.ai.Dot;
import model.wrappers.ReversiData;

import java.io.Serializable;
import java.util.Collection;

public interface ReversiManager extends Serializable {
    //Attempts to place a disk of the current color on the set point and flips the corresponding disks.
    //Returns true if the move was valid, false if the move was invalid.
    boolean movePlayer(Point point);

    //Makes a move automatically.
    //Returns the decision tree made by the ai on that move.
    Dot moveCPU();

    //Undoes a move.
    //Returns the point of the removed disk and the points of the flipped ones in a wrapper object.
    ReversiData undo();

    //Attempts to skip a turn.
    //Returns true if the turn was skipped, false if the condition for the skip weren't met.
    boolean pass();

    //Gets the value of the player on a certain point of the board.
    Player getPlayer(Point point);

    //Returns the current turn.
    Player getTurn();

    //Returns the current game state.
    GameState getState();

    //Returns the current possible moves.
    Collection<Point> getPossibleMoves();

    //Returns the amount of disks corresponding to a player.
    int getScore(Player current);

    //Returns the board size.
    int getBoardSize();
}
