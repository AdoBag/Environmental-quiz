package client.scenes;

import client.utils.ServerUtils;
import commons.Comment;
import commons.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class WaitingRoomScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Timer timer;

    @FXML
    private Button startButton;

    @FXML
    private Button quitButton;

    @FXML
    private Label waiting;

    @FXML
    private Label playersInQueue;

    @FXML
    private ImageView nukeSymbol;
    private Timer timer1;
    private long oldId;
    List<Player> players;
    Player player;
    long playerID;


    @Inject
    public WaitingRoomScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
        Image nukeImage = new Image("https://i.ibb.co/t3mQ7YS/nuclear.png");
        nukeSymbol.setImage(nukeImage);
        quitButton.setBackground(Background.EMPTY);
        startButton.setBackground(Background.EMPTY);

    }

    /**
     * Checks if anything has changed on the server.
     */
    public void checkForUpdates() {
        oldId = mainCtrl.gameId;
        checkForIdUpdate();
        Runnable task = () -> {
            Platform.runLater(() -> {
                try {
                    server.registerforMessages("/topic/comment", q -> {
                        long id = Long.parseLong(q.getText());
                        if (mainCtrl.gameId != id) {
                            mainCtrl.gameId = id;
                        }
                    });
                    Platform.setImplicitExit(false);
                }
                catch (Exception ex) {
                    ex.getStackTrace();
                }
            });
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        this.playerID = mainCtrl.playerId;
        this.player = server.getPlayerByID(playerID);
        player.setWaitingroom(true);
        server.updateIsWaitingRoom(player);
        server.registerUpdates(c -> {
            refresh();
        });
    }

    /**
     * Checks if there's a change in id's and updates the game id if true.
     */
    public void checkForIdUpdate() {
        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (oldId != mainCtrl.gameId) {
                            timer1.cancel();
                            timer1.purge();
                            decreaseNumber();
                            server.updateGameId(mainCtrl.playerId, mainCtrl.gameId);
                            oldId = mainCtrl.gameId;
                            mainCtrl.showCountdown(true);
                        }
                    }
                });
            }
        }, 0, 425);
    }

    /**
     * Stops the thread.
     */
    public void stop() {
        server.stop();
    }

    /**
     * Refreshes the label to show how many people are in the waiting room.
     */
    public void refresh() {
        this.timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        setPlayersInQueue();
                    }
                });
            }
        }, 0, 500);
    }

    /**
     * Sets the label that shows how many people are in the waiting room.
     */
    @FXML
    public void setPlayersInQueue() {
        players = server.getPlayers();
        players.removeIf(p -> p.isWaitingroom == false);
        playersInQueue.setText(players.size() + " players in the queue");
    }

    /**
     * Starts the game.
     */
    public void start() {
        mainCtrl.gameId = server.startGame();
        Comment c = new Comment();
        c.setText(String.valueOf(mainCtrl.gameId));
        server.send("/topic/comment",c);
        server.startGameTimer(mainCtrl.gameId);
    }

    /**
     * Decreases the number of players in the waiting room.
     */
    public void decreaseNumber() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        if (player != null) {
            player.setWaitingroom(false);
            server.removePlayerWaitingRoom(player);
            server.updateIsWaitingRoom(player);
        }
    }

    /**
     * Executes when the user clicks the quit button.
     */
    public void quit() {
        if (mainCtrl.returnHome()) {
            timer1.cancel();
            timer1.purge();
            decreaseNumber();
        }
    }
}
