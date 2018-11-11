package view;

import com.sun.javaws.exceptions.InvalidArgumentException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logic.ReversiGame;
import logic.ReversiManager;
import logic.ai.ScoreCornerWeightEval;
import logic.gameObjects.Player;
import utils.AI;
import utils.AlertHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.exit;

public class Main extends Application {

    private ReversiManager game;
    private AI aiOptions;
    private int boardSize;
    private Stage primarySage;
    private boolean loadedGame = false;
    @Override
    public void start(Stage primaryStage) throws Exception{
        try {
            //Load the fxml file and creat e a new stage for the task program.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("reversi.fxml"));
            Parent root = loader.load();

            //Set the stage for the task program.
            primaryStage.setTitle("Reversi");
            // primaryStage.getIcons().add(new Image("icon.png"));
            primaryStage.setScene(new Scene(root, 600, 600));
            primaryStage.setResizable(false);
            primaryStage.show();

            //Save the variables into the main class.
            this.primarySage = primaryStage;
            Controller gameController = loader.getController();
            Iterator<String> it = getParameters().getRaw().iterator();
            Map<String, String> map = new HashMap<>();
            while(it.hasNext()) {
                String key = it.next();
                String value = it.next();
                map.put(key.substring(1), value);
            }
            updateArgs(map);
            Player human = (aiOptions.getRole() == 2) ? game.getTurn() : game.getTurn().opposite();
            Player cpu = human.opposite();
            gameController.setGame(game);
            gameController.setHuman(human);
            gameController.setCpu(cpu);
            gameController.setBoardSize(boardSize);
            gameController.setAiOptions(aiOptions);
            if(loadedGame)
                gameController.loadGame();
            else
                gameController.startGame();



        }catch (IOException e) {
            e.printStackTrace();
            AlertHandler.sendErrorAlert(primaryStage, "Error loading FXML file");
        }catch (IllegalArgumentException ex){
            AlertHandler.sendErrorAlert(primaryStage, "Invalid Arguments");
            exit(1);
        }

    }
    private void updateArgs(Map<String, String> args) {
        ReversiGame oldGame = null;

        if(!Arrays.asList("size", "ai", "mode", "param", "prune", "load").containsAll(args.keySet()))
            throw new IllegalArgumentException("Invalid argument(s)");
        aiOptions = new AI();
        for(String arg : args.keySet()) {

            String value = args.get(arg);
            switch (arg) {
                case "size":
                    if(args.containsKey("load"))
                        break;
                    if(!Arrays.asList("4", "6", "8", "10").contains(value))
                        throw new IllegalArgumentException("Incorrect size");
                    boardSize = Integer.valueOf(value);
                    break;
                case "prune":
                    if(!Arrays.asList("on", "off").contains(value))
                        throw new IllegalArgumentException("Invalid prune option");
                    aiOptions.setPrune((value.equals("on")));
                    break;
                case "ai":
                    if(!Arrays.asList("0", "1", "2").contains(value))
                        throw new IllegalArgumentException("Invalid AI role");
                    aiOptions.setRole(Integer.valueOf(value));
                    break;
                case "mode":
                    if(!Arrays.asList("time", "depth").contains(value))
                        throw new IllegalArgumentException("Invalid AI mode");
                    aiOptions.setType(value);
                    break;
                case "param":
                    int param;
                    try {
                        param = Integer.valueOf(value);
                    }catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("Parameter not a number");
                    }
                    if(param <= 0)
                        throw new IllegalArgumentException("Parameter cannot be  <= 0");
                    aiOptions.setParam(param);
                    break;
                case "load":
                    try {
                        FileInputStream f = new FileInputStream(args.get("load"));
                        ObjectInputStream o = new ObjectInputStream(f);
                        oldGame = (ReversiGame) o.readObject();
                        f.close();
                        o.close();
                    } catch (IOException ex) {
                        AlertHandler.sendErrorAlert(primarySage, "Error loading game file");
                    }catch (ClassNotFoundException ex){
                        ex.printStackTrace();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid argument(s)");
            }
        }
        if(args.containsKey("load") && oldGame != null) {
            loadedGame = true;
            game = new ReversiGame(oldGame, aiOptions);
            return;
        }
        game = new ReversiGame(boardSize, aiOptions);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
