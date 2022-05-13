package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.Timer;

public class IncorrectAnswerCtrl {

    private final ServerUtils server;

    private final MainCtrl mainCtrl;

    private final GameScreenCtrl gameScreenCtrl;

    private Timer timer;

    @FXML
    private Label points;

    @FXML
    private Label correctAnswer;

    @FXML
    private Label message1;

    @FXML
    private Label message2;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView background;

    @Inject
    public IncorrectAnswerCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.gameScreenCtrl = new GameScreenCtrl(server, mainCtrl);
    }

    /**
     * Sets the incorrect screen.
     * @param correctTitle - the title of the correct answer
     */
    public void showIncorrect(String correctTitle) {
        anchorPane.setStyle("-fx-background-color: #ff9c9c");
        message1.setText("Your answer was incorrect.");
        message1.setTextFill(Color.web("#6e0000"));
        message2.setText("The correct answer was:");
        message2.setTextFill(Color.web("#6e0000"));
        correctAnswer.setText(correctTitle);
        correctAnswer.setTextFill(Color.web("#6e0000"));
        points.setText("+0 points");
        points.setTextFill(Color.web("#6e0000"));
    }

    /**
     * Sets the incorrect screen of the GuessQuestion.
     * @param correctUsage - the correct answer
     * @param pointsNo - number of gained points
     */
    public void showGuessIncorrect(long correctUsage, int pointsNo) {
        anchorPane.setStyle("-fx-background-color: #fff59c");
        message1.setTextFill(Color.web("#6e6c00"));
        message2.setTextFill(Color.web("#6e6c00"));
        correctAnswer.setText(Long.toString(correctUsage));
        correctAnswer.setTextFill(Color.web("#6e6c00"));
        points.setText("+ " + pointsNo + " points");
        points.setTextFill(Color.web("#6e6c00"));
        if (pointsNo == 0) {
            anchorPane.setStyle("-fx-background-color: #ff9c9c");
            message1.setTextFill(Color.web("#6e0000"));
            message2.setTextFill(Color.web("#6e0000"));
            correctAnswer.setTextFill(Color.web("#6e0000"));
            points.setTextFill(Color.web("#6e0000"));
        }
    }
}
