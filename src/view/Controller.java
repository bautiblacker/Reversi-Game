package view;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.*;
import logic.ai.Dot;
import logic.gameObjects.GameState;
import logic.gameObjects.Player;
import utils.AI;
import utils.AlertHandler;
import utils.Point;

import java.io.*;
import java.nio.file.Paths;

import static javafx.application.Platform.exit;

public class Controller {
    private ObservableMap<Point, Space> observableBoard;
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
    private static final int paneSize = 400;
    private GridPane gridPane;


    private Dot aiTree;
    private GameState gameState = GameState.RUNNING;
    private ReversiManager game;
    private Player human;// = Player.BLACK;
    private Player cpu;// = human.opposite();
    private AI aiOptions;// = new AI(1, "depth", 3, true);
    private int boardSize;


    public void setGame(ReversiManager game) {
        this.game = game;
    }

    public void setHuman(Player human) {
        this.human = human;
    }

    public void setCpu(Player cpu) {
        this.cpu = cpu;
    }

    public void setAiOptions(AI aiOptions) {
        this.aiOptions = aiOptions;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public void initialize() {
    }

    public void start() {
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

        System.out.println("hizo restartGame");
        System.out.println(aiOptions);
        observableBoard.clear();
        gridPane.getChildren().clear();
        System.out.println("boardsize: "+ boardSize);
        game = new ReversiGame(boardSize, aiOptions);
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
                if(game.getTurn() == human || aiOptions.getRole() == 0)
                if(game.movePlayer(k)) {
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
    public void handleSaveButtonAction() {
        Stage currentStage = (Stage) ap.getScene().getWindow();
        if(aiTree == null)
            AlertHandler.sendInformationConfirmation(currentStage, "Warning", "No data to save.");

        File selectedFile = getFileChooser().showSaveDialog(new Stage());
        if(selectedFile != null) {
            try {
                saveData(selectedFile);
                AlertHandler.sendConfirmationAlert(currentStage,
                        "Dot file successfully saved");
            }catch (Exception ex) {
                ex.printStackTrace();
                AlertHandler.sendErrorAlert(currentStage, "Error saving file");
            }
        }
    }

    @FXML
    public void handleUndoButtonAction() {
        if (game.undo() != null) {
            gameState = game.getState();
            drawBoard();
        }
    }

    @FXML
    protected void handleTreeButtonAction() {
        Stage currentStage = (Stage) ap.getScene().getWindow();
        if(aiTree == null)
            AlertHandler.sendInformationConfirmation(currentStage, "Warning", "No data to save.");

        File selectedFile = getFileChooser().showSaveDialog(new Stage());
        if(selectedFile != null) {
            try {
                PrintWriter out = new PrintWriter(selectedFile);
                out.println(Dot.tree(aiTree));
                out.close();
                AlertHandler.sendConfirmationAlert(currentStage,
                        "Dot file successfully saved");
            }catch (Exception ex) {
                AlertHandler.sendErrorAlert(currentStage, "Error saving file");
            }
        }
    }


    @FXML
    public void handleAIMove() {
        if(game.getTurn() == cpu) {
            aiTree = game.moveCPU();
            gameState = game.getState();
            drawBoard();
        }
    }

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

    private FileChooser getFileChooser() {
        //Opens the file chooser at the default location as the current location.
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(Paths.get(".").toFile());
        return chooser;
    }
    private void saveData(File file) throws IOException {
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(game);
        o.close();
        f.close();
    }

}