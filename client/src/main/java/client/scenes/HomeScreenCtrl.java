package client.scenes;

import client.utils.ServerUtils;
import commons.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button adminButton;

    @FXML
    private Label adminLabel;

    @FXML
    private ImageView background;

    @FXML
    private Label i;

    @FXML
    private Button lbButton;

    @FXML
    private Label lbLabel;

    @FXML
    private Button multipButton;

    @FXML
    private Label multipLabel;

    @FXML
    private Label q;

    @FXML
    private Button quitButton;

    @FXML
    private Button singlepButton;

    @FXML
    private Label singlepLabel;

    @FXML
    private Label u;

    @FXML
    private Label z1;

    @FXML
    private Label z2;

    @FXML
    private Label z3;

    @FXML
    private ImageView nukeSymbol;

    @FXML
    private AnchorPane anchorPane;

    Player player;
    long playerID;

    @Inject
    public HomeScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initializes the scene.
     * @param location the URL of the scene
     * @param resources other resources used for the scene
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /** AB: Exceptions are thrown when trying to load this */
        Image nukeImage = new Image("https://i.ibb.co/t3mQ7YS/nuclear.png");
        nukeSymbol.setImage(nukeImage);
        quitButton.setBackground(Background.EMPTY);
    }

    /**
     * Sets this.player to the current player object.
     */
    public void findThisPlayer() {
        this.playerID = mainCtrl.playerId;
        this.player = server.getPlayerByID(playerID);
    }

    /**
     * Launches the confirmation for the single player game mode.
     * Called when the users presses 'SINGLEPLAYER'
     */
    public void singleplayer() {
        if (checkActivityBankSize()) {
            mainCtrl.startConfirmation(false);
        }
    }

    /**
     * Shows the waiting room screen.
     * Called when the users presses 'MULTIPLAYER'
     */
    public void multiplayer() {
        if (checkActivityBankSize()) {
            findThisPlayer();
            server.addPlayerWaitingRoom(this.player);
            mainCtrl.showWaiting();
        }
    }

    /**
     * Checks if there are enough activities in the bank.
     * @return true if enough and false otherwise
     */
    public boolean checkActivityBankSize() {
        if (server.getCount() < 60) {
            Alert notEnoughActivities = new Alert(Alert.AlertType.WARNING);
            notEnoughActivities.setHeaderText("There must be at least 60 activities in the server to start the game");
            notEnoughActivities.show();
            return false;
        }
        return true;
    }

    /**
     * Shows the all-time leaderboard screen.
     * Called when the users presses 'LEADERBOARD'
     */
    public void leaderboard() {
        mainCtrl.showLeaderboard(false);
    }

    /**
     * Shows the admin screen.
     * Called when the use presses 'ADMIN'
     */
    public void admin() {
        mainCtrl.showAdmin();
    }

    /**
     * Allows the user to quit the game.
     * Called when the user presses the 'nuclear' button
     */
    public void quit() {
        mainCtrl.quitApp();
    }

    /**
     * called whenever a player closes the application. This sets their
     * isOnline boolean to false, to allow this username to be used again
     */
    public void setPlayerOffline() {
        server.leave(mainCtrl.nickname);
    }

    /**
     * Deals with the user's keyboard input.
     * @param e the key that was pressed.
     */
    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            quit();
        }
        if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.NUMPAD1) {
            singleplayer();
        }
        if (e.getCode() == KeyCode.M || e.getCode() == KeyCode.NUMPAD2) {
            multiplayer();
        }
        if (e.getCode() == KeyCode.L || e.getCode() == KeyCode.NUMPAD3) {
            leaderboard();
        }
        if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.NUMPAD4) {
            admin();
        }
    }
}