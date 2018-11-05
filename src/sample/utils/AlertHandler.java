
package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.Player;

import java.util.Optional;

public class AlertHandler {

    public static boolean sendGameOverAlert(Stage stage, Player player) {
        String message;
        if(player == Player.NONE)
            message = "It's a draw!";
        else
            message = player + " won!";
        String okText = "Restart";
        String notOkText = "Exit";
        return sendAlert(stage, player, message, okText, notOkText);

    }
    public static boolean sendOutOfMovesAlert(Stage stage, Player player) {
        String message = player + " is out of moves.";
        String okText = "Pass";
        String notOkText = "Undo";
        return sendAlert(stage, player, message, okText, notOkText);
    }

    private static boolean sendAlert(Stage stage, Player player, String message, String okText, String notOkText){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message);
        alert.initOwner(stage);
        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(okText);

        Button cancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setText(notOkText);

        Optional<ButtonType> option = alert.showAndWait();
        return ButtonType.OK.equals(option.orElse(null));
    }
}