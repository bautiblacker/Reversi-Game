package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import model.Player;
import model.ReversiData;
import model.ReversiGame;
import model.ReversiManager;
import utils.AI;
import utils.Point;
import sample.Space;
import java.util.*;

public class Controller {
    private ObservableMap<Point, Space> observableBoard = FXCollections.observableHashMap();
    private Collection<Point> flipped;

    @FXML
    private Text textToShow;
    @FXML
    private BorderPane basePane;

    private Map<Point, Space> board;
    private GridPane gridPane;
    private ReversiManager game = new ReversiGame(8, new AI(0, null, 0, false));



    public void initialize() {
        board = new HashMap<>(8*8);
        gridPane = new GridPane();
        basePane.setCenter(gridPane);
        restartGame();
    }

    @FXML
    private void restartGame() {
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                Point current = new Point(j, i);
                Space space = new Space();
                observableBoard.put(current, space);
            }
        }
        observableBoard.forEach((k,v) -> {
            v.setOnMouseClicked(e ->
                {
                setFlipped(game.move(k));
                if( flipped != null ) {
                 v.updateImage(game.getPlayer(k));
                 drawBoard();
            }
        });
            v.updateImage(game.getPlayer(k));
            gridPane.add(v, k.getY(), k.getX());
        });
    }

    public void drawBoard() {
        if(flipped != null)
            for(Point point : flipped)
             observableBoard.get(point).updateImage(game.getPlayer(point));
    }

    public void setFlipped(Collection<Point> flipped) {
        this.flipped = flipped;
    }


    @FXML
    public void handlePassButtonAction(ActionEvent event) { textToShow.setText("Pass Button Pressed"); }

    @FXML
    public
    void handleUndoButtonAction(ActionEvent event) {
        //TODO FIX
        ReversiData aux = game.undo();
        if(aux != null) {
            for (Point point : aux.getFlipped()) {
                observableBoard.get(point).updateImage(game.getPlayer(point));
            }
            observableBoard.get(aux.getPlaced()).updateImage(game.getPlayer(aux.getPlaced()));
        }
        drawBoard();
    }

    @FXML
    protected void handleTreeButtonAction(ActionEvent event) { textToShow.setText("Pass Tree Pressed"); }

}
