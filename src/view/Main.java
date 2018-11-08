package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("reversi.fxml"));
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
