package view;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Player;

public class Space extends Button {
    private final int width = 22;
    private final int height = 22;
    private ImageView background = new ImageView(new Image("background.png", width, height,
            true, true));
    private ImageView black = new ImageView(new Image("black.png", width, height,
            true, true));
    private ImageView white = new ImageView(new Image("white.png", width, height,
            true, true));
    private ImageView possible = new ImageView(new Image("possible.png", width, height,
            false, true));

    public void updateImage(Player player) {

        switch (player) {
            case NONE:
                setGraphic(background);
                break;
            case BLACK:
                setGraphic(black);
                break;
            case WHITE:
                setGraphic(white);
                break;
        }
    }

    public void setToPossible() {
        setGraphic(possible);
    }
}
