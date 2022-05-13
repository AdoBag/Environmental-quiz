package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Player;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

public class LeaderboardCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private ImageView background;

    @FXML
    private ImageView nukeSymbol;

    @FXML
    private Label p1;

    @FXML
    private Label p1Score;

    @FXML
    private Label p2;

    @FXML
    private Label p2Score;

    @FXML
    private Label p3;

    @FXML
    private Label p3Score;

    @FXML
    private Label p4;

    @FXML
    private Label p4Score;

    @FXML
    private Label p5;

    @FXML
    private Label p5Score;

    @FXML
    private Label p6;

    @FXML
    private Label p6Score;

    @FXML
    private Label p7;

    @FXML
    private Label p7Score;

    @FXML
    private Label p8;

    @FXML
    private Label p8Score;

    @FXML
    private Label personalPlace;

    @FXML
    private Label personalScore;

    @FXML
    private Button quitButton;

    @FXML
    private Label title;

    private List<Label> names;
    private List<Label> scores;
    private boolean endScreen;
    private boolean isAlltime;

    @Inject
    public LeaderboardCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the scene.
     * @param location - the URL of the scene
     * @param resources - other resources used for the scene
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainCtrl.loadQuitButton(nukeSymbol, quitButton);
        this.names = Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8);
        this.scores = Arrays.asList(p1Score, p2Score, p3Score, p4Score, p5Score, p6Score, p7Score, p8Score);
        server.checkForUpdates((Consumer<Player>) player -> setLeaderboard(true, false));

    }

    /**
     * Shows a prompt for playing again or returning to homescreen.
     */
    public void endScreen() {
        Alert.AlertType type = Alert.AlertType.CONFIRMATION;
        Alert alert = new Alert(type, "Do you want to play again or go to the homescreen?");
        alert.getDialogPane().setHeaderText("");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Home Screen");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Play Again");
        Optional<ButtonType> res = alert.showAndWait();
        if (res.get() == ButtonType.OK) {
            mainCtrl.showHome();
        }
        else if (res.get() == ButtonType.CANCEL) {
            if (isAlltime) {
                mainCtrl.showCountdown(false);
            }
            else mainCtrl.showWaiting();
        }
    }

    /**
     * Return to homescreen and stop refreshing.
     */
    public void quit() {
        if (endScreen) {
            endScreen();
        } else {
            if (!isAlltime) {
                mainCtrl.quitIntermediateLb();
            }
            mainCtrl.returnHome();
        }
    }

    /**
     * Retrieves all players (sorted based on (high)score) from the server/game.
     * sets name and score of top 8 in the table
     * find own place and score and put it at bottom
     * @param isAllTime - true for all-time leaderboard
     * @param isEndScreen - true if the leaderboard is the last scene
     */
    public void setLeaderboard(boolean isAllTime, boolean isEndScreen) {
        this.endScreen = isEndScreen;
        this.isAlltime = isAllTime;
        List<Player> players;
        if (isAllTime) {
            players = server.getLeaderboard();
            players.removeIf(p -> (p.highscore == 0));
        }
        else {
            players = server.getIGLeaderboard(mainCtrl.gameId);
        }
        for (int i = 0; i < names.size(); i++) {
            if (players.size() > i) {
                names.get(i).setText(players.get(i).nickname);
                if (isAllTime) {
                    scores.get(i).setText(Integer.toString(players.get(i).highscore));
                }
                else {
                    scores.get(i).setText(Integer.toString(players.get(i).score));
                }
            }
            else {
                names.get(i).setText("-");
                scores.get(i).setText("-");
            }
        }
        int place = 0;
        int score = 0;
        for (Player player : players) {
            if (player.nickname.equals(mainCtrl.nickname)) {
                place = players.indexOf(player) + 1;
                if (isAllTime) {
                    score = player.highscore;
                }
                else {
                    score = player.score;
                }
                break;
            }
        }
        personalPlace.setText(place + "/" + players.size());
        personalScore.setText(Integer.toString(score));
        if (score == 0 && !isAllTime) {
            personalPlace.setText("N.A.");
            personalScore.setText("0");
        }
    }
    
}

