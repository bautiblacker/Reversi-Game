package sample;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Player;
import model.ReversiGame;
import model.ReversiManager;
import utils.Point;

import java.util.Collection;


public class Space extends Button {
    private Point point;
    private ReversiManager game;
    private Controller controller;
    private Player player;


    public Space(Point point, ReversiManager game, Controller controller) {
        this.point = point;
        this.game = game;
        this.controller = controller;
        setOnMouseClicked(e ->{
            Collection<Point> flipped = game.move(point);
            if( flipped != null ) {
                updateImage(game.getPlayer(point));
                controller.setFlipped(flipped);
                controller.drawBoard();
            }
        });
    }

    public void updateImage(Player player) {
        final int width = 15;
        final int height = 15;
        ImageView background = new ImageView(new javafx.scene.image.Image("background.png", width, height,
                true, true));
        ImageView black = new ImageView(new javafx.scene.image.Image("black.png", width, height,
                true, true));
        ImageView white = new ImageView(new Image("white.png", width, height,
                true, true));

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
}
