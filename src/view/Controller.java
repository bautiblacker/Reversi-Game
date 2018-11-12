package view;


import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.logic.ReversiGame;
import model.ReversiManager;
import model.ai.Dot;
import model.GameState;
import model.Player;
import model.wrappers.AI;
import view.utils.AlertHandler;
import model.Point;

import java.io.*;
import java.nio.file.Paths;

import static javafx.application.Platform.exit;

public class Controller {
    @FXML
    private BorderPane basePane;
    @FXML
    private Button moveAiButton = new Button();
    @FXML
    private Text whiteScore = new Text();
    @FXML
    private Text whiteText = new Text();
    @FXML
    private Text blackScore = new Text();
    @FXML
    private Text blackText = new Text();
    @FXML
    private Text turnsLeft = new Text();
    @FXML
    private AnchorPane ap;

    private ObservableMap<Point, Space> observableBoard;
    private static final int paneSize = 400;
    private GridPane gridPane;

    private Dot aiTree;
    private GameState gameState = GameState.RUNNING;
    private ReversiManager game;
    private Player human;
    private Player cpu;
    private AI aiOptions;
    private int boardSize;

    @FXML
    public void handleSaveButtonAction() {
        Stage currentStage = (Stage) ap.getScene().getWindow();
        File selectedFile = getFileChooser().showSaveDialog(new Stage());
        if(selectedFile != null) {
            try {
                saveData(selectedFile);
                AlertHandler.sendConfirmationAlert(currentStage,
                        "Game file successfully saved");
            }catch (Exception ex) {
                ex.printStackTrace();
                AlertHandler.sendErrorAlert(currentStage, "Error saving file");
            }
        }
    }

    @FXML
    public void handleTreeButtonAction() {
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
    public void handleUndoButtonAction() {
        if (game.undo() != null) {
            gameState = game.getState();
            drawBoard();
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

    public void startGame() {
        start();
        observableBoard.clear();
        gridPane.getChildren().clear();
        game = new ReversiGame(boardSize, aiOptions);
        gameState = GameState.RUNNING;
        buildObservableMap(game.getBoardSize());
        setButtonsAction();
        drawBoard();
        updateScores();
        setPlayerText();

    }

    public void loadGame() {
        start();
        observableBoard.clear();
        gridPane.getChildren().clear();
        gameState = game.getState();
        boardSize = game.getBoardSize();
        buildObservableMap(boardSize);
        setButtonsAction();
        drawBoard();
        updateScores();
        setPlayerText();
    }

    private void setPlayerText() {
        if(aiOptions.getRole() == 0){
            blackText.setText("human");
            whiteText.setText("human");
        }
        else if(human == Player.BLACK){
            blackText.setText("human");
            whiteText.setText("cpu");
        }
        else{
            blackText.setText("cpu");
            whiteText.setText("human");
        }
    }

    private void start() {
        observableBoard = FXCollections.observableHashMap();
        gridPane = new GridPane();
        gridPane.setPrefHeight(paneSize);
        gridPane.setPrefWidth(paneSize);
        basePane.setCenter(gridPane);
        gridPane.setAlignment(Pos.TOP_CENTER);
        if(aiOptions.getRole() == 0)
            moveAiButton.setDisable(true);
        else
            moveAiButton.setDisable(false);
    }

    private void drawBoard() {
        observableBoard.forEach( (k, v) -> v.updateImage(game.getPlayer(k)));
        for(Point point : game.getPossibleMoves())
            observableBoard.get(point).setToPossible();
        updateScores();
        checkGameState(gameState);
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
            startGame();
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

    private void setButtonsAction() {
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
    }

    private void buildObservableMap(int boardSize) {
        for (int i = 0; i < boardSize; i++){
            for (int j = 0; j < boardSize; j++){
                Point current = new Point(j, i);
                Space space = new Space();
                observableBoard.put(current, space);
            }
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