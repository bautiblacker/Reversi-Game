package sample;


import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;

import java.io.File;

public class Controller {
    @FXML private Text textToShow;

    @FXML private GridPane gridPane;

    private double gridPaneCellLength = 62;
    private double gridPaneCellHeight = 37;

    File blackFile = new File("src/utils/black_circle.jpg");
    Image blackImage = new Image(blackFile.toURI().toString());

    @FXML private void handleGridPanePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        Integer colIndex = (int)(x/gridPaneCellLength);
        Integer rowIndex = (int)(y/gridPaneCellHeight);
        textToShow.setText("Apretaste el grid pane con col: " + colIndex + " y row: " + rowIndex);
        // gridPane.getStyleClass().add("red");

        // gridPane.setStyle("-fx-background-color:#eeeeee; -fx-opacity:1;");

    }


    @FXML protected void handlePassButtonAction(ActionEvent event) { textToShow.setText("Pass Button Pressed"); }

    @FXML protected void handleUndoButtonAction(ActionEvent event) { textToShow.setText("Pass Undo Pressed"); }

    @FXML protected void handleTreeButtonAction(ActionEvent event) { textToShow.setText("Pass Tree Pressed"); }



}
