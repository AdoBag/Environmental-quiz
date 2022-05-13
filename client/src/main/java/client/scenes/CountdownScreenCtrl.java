package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Question;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.Timer;
import java.util.TimerTask;

public class CountdownScreenCtrl {

    private final MainCtrl mainCtrl;
    private final ServerUtils server;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label message;

    private Timer timer;

    private byte timesShown;

    @Inject
    public CountdownScreenCtrl(MainCtrl mainCtrl, ServerUtils server) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Shows and controls the countdown animations and then proceeds to the game.
     * @param isMultiplayer true if this is a multiplayer game and false otherwise
     */
    public void showCountdown(boolean isMultiplayer) {
        timesShown = 0;
        timer = new Timer();
        message.setText("Get set");
        message.setTextFill(Color.web("#fdd609"));
        anchorPane.setStyle("-fx-background-color: #000000;");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    switch (timesShown) {
                        case 0:
                            break;
                        case 1:
                            message.setText("Get ready");
                            break;
                        case 2:
                            message.setTextFill(Color.web("#000000"));
                            anchorPane.setStyle("-fx-background-color: #fdd609;");
                            message.setText("Go");
                            break;
                        default:
                            timer.cancel();
                            timer.purge();
                            if (!isMultiplayer) {
                                server.nextRound(mainCtrl.gameId);
                            }
                            Question question = server.getNextQuestion(mainCtrl.gameId);
                            if (!isMultiplayer) {
                                mainCtrl.showGame(question);
                            } else {
                                mainCtrl.showMultiplayer(question);
                            }
                    }
                    timesShown++;
                });
            }
        }, 500, 1000);
    }
}
