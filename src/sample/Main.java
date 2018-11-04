package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Reversi v1.0");
        primaryStage.setScene(new Scene(root, 680, 600));
        //tipo tp poo
        //loader.getcontroller.setReversiGame(new ReversiGame() )
        //getAI(getParameters)
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
