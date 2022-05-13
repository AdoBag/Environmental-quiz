package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.Timer;

public class CorrectAnswerCtrl {

    private Timer timer;

    @FXML
    private Label points;

    @FXML
    private Label message;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView background;

    @Inject
    public CorrectAnswerCtrl(ServerUtils server, MainCtrl mainCtrl) {
    }

    /*
    PS: Shows the screen for 3 seconds, for testing purposes it goes to admin after 3 seconds,
    when timer is implemented it needs to be changed to go to the next question
     */
    public void showCorrect(int pointsNo) {
        anchorPane.setStyle("-fx-background-color: #aeff9c");
        message.setText("Yes! That was the correct answer.");
        message.setTextFill(Color.web("#005e09"));
        points.setText("+" + String.valueOf((pointsNo)) + " points");
        points.setTextFill(Color.web("#005e09"));
    }
}
