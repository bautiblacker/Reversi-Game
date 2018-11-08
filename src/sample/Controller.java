package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.*;
import utils.AI;
import utils.AlertHandler;
import utils.Point;
import sample.Space;
import java.util.*;

import static javafx.application.Platform.exit;

public class Controller {
    private ObservableMap<Point, Space> observableBoard;

    @FXML
    private Text textToShow;
    @FXML
    private BorderPane basePane;
    @FXML
    private Text whiteScore = new Text();
    @FXML
    private Text blackScore = new Text();
    @FXML
    private Text turnsLeft = new Text();
    @FXML
    private AnchorPane ap;


    private GridPane gridPane;
    @Deprecated
    private static final int boardSize =10;
    private ReversiManager game;
    private static final int paneSize = 400;
    private GameState gameState = GameState.RUNNING;


    public void initialize() {
        observableBoard = FXCollections.observableHashMap();
        gridPane = new GridPane();
        gridPane.setPrefHeight(paneSize);
        gridPane.setPrefWidth(paneSize);
        basePane.setCenter(gridPane);
        gridPane.setAlignment(Pos.TOP_CENTER);

        restartGame(boardSize);
        updateScores();
    }


    private void restartGame(int size) {
        observableBoard.clear();
        gridPane.getChildren().clear();
        game = new ReversiGame(boardSize, new AI(1, "depth", 5, true));
        gameState = GameState.RUNNING;
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                Point current = new Point(j, i);
                Space space = new Space();
                observableBoard.put(current, space);
            }
        }
        observableBoard.forEach((k,v) -> {
            v.setOnMouseClicked(e -> {
                if(game.move(k)) {
                    v.updateImage(game.getPlayer(k));
                    gameState = game.getState();
                    drawBoard();
                }
            });
            gridPane.add(v, k.getY(), k.getX());
        });
        drawBoard();
    }

    private void drawBoard() {
        observableBoard.forEach( (k, v) -> v.updateImage(game.getPlayer(k)));
        //TODO extract possible.png into Space.
        for(Point point : game.getPossibleMoves())
            observableBoard.get(point).updateImage("possible.png");
        updateScores();
        checkGameState(gameState);
    }


    @FXML
    public void handleSaveButtonAction(ActionEvent event) { textToShow.setText("Save Button Pressed"); }

    @FXML
    public void handleUndoButtonAction() {
        if (game.undo() != null) {
            gameState = game.getState();
            drawBoard();
        }
    }

    @FXML
    protected void handleTreeButtonAction(ActionEvent event) { textToShow.setText("Pass Tree Pressed"); }

    private void checkGameState(GameState gameState){
        switch (gameState) {
            case GAME_OVER:
                gameOver();
                break;
                case OUT_OF_MOVES:
                    outOfMoves(game.getTurn());

        }
    }

    private void gameOver() {
        Stage currentStage = (Stage) ap.getScene().getWindow();
        int black = Integer.valueOf(blackScore.getText());
        int white = Integer.valueOf(whiteScore.getText());
        boolean restart;
        if(black == white)
            restart = AlertHandler.sendGameOverAlert(currentStage, Player.NONE);
        else {
            Player winner = (black > white) ? Player.BLACK : Player.WHITE;
            restart = AlertHandler.sendGameOverAlert(currentStage, winner);
        }
        if(restart)
            restartGame(boardSize);
        else
            exit();
    }

    private void outOfMoves(Player player) {
        Stage currentStage = (Stage) ap.getScene().getWindow();
        boolean ok = AlertHandler.sendOutOfMovesAlert(currentStage, player);
        if(ok) {
            handlePassAction();
        }
        else
            handleUndoButtonAction();
    }
    private void handlePassAction() {
        game.pass();
        gameState = game.getState();
        drawBoard();
    }

    private void updateScores() {
        blackScore.setText(String.valueOf(game.getScore(Player.BLACK)));
        whiteScore.setText(String.valueOf(game.getScore(Player.WHITE)));
        turnsLeft.setText(String.valueOf(game.getScore(Player.NONE)));

        switch (game.getTurn()) {
            case WHITE:
                whiteScore.setUnderline(true);
                blackScore.setUnderline(false);
                break;
            case BLACK:
                whiteScore.setUnderline(false);
                blackScore.setUnderline(true);
                break;
        }
    }
}