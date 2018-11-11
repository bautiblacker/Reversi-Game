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
import static java.lang.System.setErr;

public class Main extends Application {

    private ReversiManager game;
    private AI aiOptions;
    private int boardSize;
    private Stage primarySage;
    private boolean loadedGame = false;
    private ReversiGame oldGame;
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
                String aux;
                aux = it.next();
                if(aux.charAt(0) == '-')
                    if(it.hasNext())
                        map.put(aux.substring(1), it.next());
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

            if(aiOptions.getType().equals("depth") && aiOptions.getParam() > ReversiGame.DEPTH_HARD_LIMIT)
                System.err.println("WARNING: depth is larger than maximum depth. Depth will be set as "
                        + ReversiGame.DEPTH_HARD_LIMIT + ".");

        }catch (IOException e) {
            e.printStackTrace();
            AlertHandler.sendErrorAlert(primaryStage, "Error loading FXML file");
        }catch (IllegalArgumentException ex){
            AlertHandler.sendErrorAlert(primaryStage, ex.getMessage());
            exit(1);
        }

    }
    private void updateArgs(Map<String, String> args) {

        if(!Arrays.asList("size", "ai", "mode", "param", "prune", "load").containsAll(args.keySet()))
            throw new IllegalArgumentException("Invalid argument(s).");
        if(args.keySet().size() < 2)
            throw new IllegalArgumentException("Missing arguments.");
        aiOptions = new AI();
        if(validateAi(args.get("ai")))
            aiOptions.setRole(Integer.valueOf(args.get("ai")));
        else
            throw new IllegalArgumentException("Invalid AI role.");

        if(args.containsKey("load"))
            attemptLoad(args.get("load"));
        else{
            if(validateSize(args.get("size")))
                boardSize = Integer.valueOf(args.get("size"));
            else
                throw new IllegalArgumentException("Invalid board size (must be: 4, 6, 8 or 10).");
        }
        if(!args.get("ai").equals("0")){
            if(validateMode(args.get("mode")))
                aiOptions.setType(args.get("mode"));
            else
                throw new IllegalArgumentException("Invalid AI mode.");

            if(validateParam(args.get("param")))
                aiOptions.setParam(Integer.valueOf(args.get("param")));
            else
                throw new IllegalArgumentException("Parameter is missing or an invalid number.");

            if(validatePrune(args.get("prune")))
                aiOptions.setPrune((args.get("prune").equals("on")));
            else
                throw new IllegalArgumentException("Invalid prune option.");
        }

        if(game == null)
            game = new ReversiGame(boardSize, aiOptions);

    }

    private boolean validateSize(String value) {
        return Arrays.asList("4", "6", "8", "10").contains(value);
    }

    private boolean validateMode(String value) {
        return Arrays.asList("time", "depth").contains(value);
    }

    private boolean validateParam(String value) {
        int param;
        try {
            param = Integer.valueOf(value);
        }catch (NumberFormatException ex) {
            return false;
        }
        return param > 0;
    }

    private boolean validatePrune(String value) {
        return Arrays.asList("on", "off").contains(value);
    }

    private boolean validateAi(String value) {
        return Arrays.asList("0", "1", "2").contains(value);
    }

    private void attemptLoad(String value) {
        try {
            FileInputStream f = new FileInputStream(value);
            ObjectInputStream o = new ObjectInputStream(f);
            oldGame = (ReversiGame) o.readObject();
            f.close();
            o.close();
        } catch (IOException ex) {
            AlertHandler.sendErrorAlert(primarySage, "Error loading game file");
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
        loadedGame = true;
        game = new ReversiGame(oldGame, aiOptions);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
