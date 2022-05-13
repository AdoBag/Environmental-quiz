package client.scenes;

import client.utils.ServerUtils;
import commons.Comment;
import commons.Player;
import commons.Question;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.skins.FlatSkin;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


public abstract class QuestionScreenCtrl {
    @FXML
    protected StackPane countdown;

    @FXML
    protected Button emoji1;

    @FXML
    protected ImageView emojiImage1;

    @FXML
    protected Button emoji2;

    @FXML
    protected ImageView emojiImage2;

    @FXML
    protected Button emoji3;

    @FXML
    protected ImageView emojiImage3;

    @FXML
    protected Button emoji4;

    @FXML
    protected ImageView emojiImage4;

    @FXML
    protected Button emoji5;

    @FXML
    protected ImageView emojiImage5;

    @FXML
    protected Button emoji6;

    @FXML
    protected ImageView emojiImage6;

    @FXML
    protected Button joker1;

    @FXML
    protected Button joker2;

    @FXML
    protected Button joker3;

    @FXML
    protected Label pointsLabel;

    @FXML
    protected ListView reactionTable;

    @FXML
    protected ButtonBar reactionBar;

    @FXML
    protected Label roundCounterLabel;

    @FXML
    protected Button quitButton;

    @FXML
    protected ImageView quitButtonImage;

    protected final ServerUtils server;
    protected final MainCtrl mainCtrl;

    boolean multiplayer;

    //protected Timer t;
    protected final byte questionCount = 20;
    protected static byte roundCounter;

    protected List<Player> players; // AB: do we really need this here ?
    protected Player player;

    protected int playerPoints;

    public Timer returnTimer;
    protected Timer nextRoundTimer;
    protected Timer intermediateScreenTimer;
    protected Timer t;
    protected int count;
    protected Gauge gauge;

    protected int delay;
    protected int period;

    protected int pointMultiplier;          // Used for the double points joker

    protected double maxTime = 10.0d;
    protected double passedTime = 0.1d;
    protected double selectedTime;          // Used to save the time when an answer is selected



    public QuestionScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    public void initialize(URL location, ResourceBundle resources) {
        Image nukeImage = new Image("https://i.ibb.co/t3mQ7YS/nuclear.png");
        quitButtonImage.setImage(nukeImage);
        quitButton.setBackground(Background.EMPTY);

        gauge = new Gauge();
        gauge.setSkin(new FlatSkin(gauge));
        gauge.setUnit("seconds");
        gauge.setMaxValue(10);
        gauge.setMinValue(0.01);
        countdown.getChildren().add(gauge);
        maxTime = 10.0d;
        passedTime = 0.0d;
        pointMultiplier = 1;
        multiplayer = false;
    }

    /**
     * Transfers data from other controller to this.
     * @param other the other controller
     */
    public void transferData(QuestionScreenCtrl other) {
        this.roundCounter = other.getRoundCounter();
        this.player = other.getPlayer();
        this.playerPoints = other.getPlayerPoints();
        this.nextRoundTimer = other.getNextRoundTimer();
        if (!other.getJoker1IsVisible()) joker1.setVisible(false);
        if (!other.getJoker2IsVisible()) joker2.setVisible(false);
        if (!other.getJoker3IsVisible()) joker3.setVisible(false);
        this.multiplayer = other.multiplayer;
        this.reactionTable.setItems(other.reactionTable.getItems());


    }

    public static byte getRoundCounter() {
        return roundCounter;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPlayerPoints() {
        return playerPoints;
    }

    public int getDelay() {
        return delay;
    }

    public int getPeriod() {
        return period;
    }

    public int getPointMultiplier() {
        return pointMultiplier;
    }

    public Timer getNextRoundTimer() {
        return nextRoundTimer;
    }

    public boolean getJoker1IsVisible() {
        return joker1.isVisible();
    }

    public boolean getJoker2IsVisible() {
        return joker2.isVisible();
    }

    public boolean getJoker3IsVisible() {
        return joker3.isVisible();
    }

    /**
     * Setter for the roundCounterLabel to display the round number on the (right-up of the) screen.
     * */
    public void setRoundCounterLabel() {
        roundCounterLabel.setText("Q " + roundCounter + "/" + questionCount);
    }

    /**
     * Setter for the playerPointsLabel to display the amount of points the player has on the screen.
     */
    public void setPlayerPointsLabel() {
        playerPoints = player.getScore();
        pointsLabel.setText(Integer.toString(playerPoints));
    }

    protected void updateServerScore() {
        player.updateScore();
        server.updateScore(player);
    }

    public void hideMultiplayerItems() {
        reactionTable.setVisible(false);
        reactionBar.setVisible(false);
    }

    /**
     * This function is used to return a new comment that will be uploaded for the chat messages.
     * @param name What the author of the comment
     * @param text the text of the comment.
     * @return The comment createed with those fields
     */
    public Comment makeComment(String name, String text) {
        Comment c = new Comment();
        c.setName(name);
        c.setText(text);
        return c;
    }

    public void scrollTo() {
        /** AB: the display needs to be fixed */
        reactionTable.scrollTo(reactionTable.getItems().size() + 1);
    }

    /**
     * This functions returns a string that is used as destination point for a message.
     * @param path Path of the server
     * @param id Id of the game
     * @return The path of the link for messages
     */
    public String linkforMessages(String path, long id) {
        path = path + String.valueOf(id);
        return path;
    }

    /**
     * Function used to send the angry emoji.
     */
    public void angry() {
        server.send(linkforMessages("/topic/comment/", player.gameId),
                makeComment(mainCtrl.nickname, "\uD83D\uDE21"));
        scrollTo();

    }

    /**
     * Function used to send the laughing emoji.
     */
    public void laughing() {
        server.send(linkforMessages("/topic/comment/", player.gameId),
                makeComment(mainCtrl.nickname, "\uD83D\uDE02"));
        scrollTo();
    }

    /**
     * Function used to send the devil emoji.
     */
    public void devil() {
        server.send(linkforMessages("/topic/comment/", player.gameId),
                makeComment(mainCtrl.nickname, "\uD83D\uDE08"));
        scrollTo();
    }

    /**
     * Function used to send the crying emoji.
     */
    public void crying() {
        server.send(linkforMessages("/topic/comment/", player.gameId),
                makeComment(mainCtrl.nickname, "\uD83D\uDE2D"));
        scrollTo();
    }

    /**
     * Function used to send the sunglasess emoji.
     */
    public void sunglasses() {
        server.send(linkforMessages("/topic/comment/", player.gameId),
                makeComment(mainCtrl.nickname, "\uD83D\uDE0E"));
        scrollTo();
    }

    /**
     * Function used to send the heart eyes emoji.
     */
    public void heart() {
        server.send(linkforMessages("/topic/comment/", player.gameId),
                makeComment(mainCtrl.nickname, new StringBuilder().appendCodePoint(0x1F60D).toString()));
        scrollTo();
    }

    /**
     * Turns a byte array into an image.
     * @param source a byte array with the image data
     * @return an image representing an activity
     */
    public Image decodeImg(byte[] source) {
        Image s = new Image(new ByteArrayInputStream(source));
        return s;
    }

    public void createGame() {
        mainCtrl.gameId = server.startGame();
        server.updateGameId(mainCtrl.playerId, mainCtrl.gameId);
    }

    /**
     * Starts a new game.
     * @param multiplayer boolean that is true if this game is MP
     * @param thisCtrl this Controller
     * @param question the question to be displayed
     */
    public void startGame(boolean multiplayer, QuestionScreenCtrl thisCtrl, Question question) {
        this.multiplayer = multiplayer;
        this.nextRoundTimer = new Timer();
        roundCounter = 0;                   //MW: the counter to tell at which question the player currently is
        playerPoints = 0;                   //MW: the player starts with 0 points each game
        delay = 3000;
        period = 10000;
        count = 0;

        for (int i = 0; i < 14; i++) {
            reactionTable.getItems().add(" ");
        }
        Runnable task = () -> {
            Platform.runLater(() -> {
                try {
                    server.registerforMessages(linkforMessages("/topic/comment/", player.gameId), q -> {
                        reactionTable.getItems().add(q.getName() + ":");
                        reactionTable.getItems().add(q.getText());
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


        player = server.getPlayerByID(mainCtrl.playerId);
        player.score = 0;
        server.updateScore(player);
        resetJokers();
        pointsLabel.setText("0");
        if (t != null) {
            t.cancel();
            t.purge();
        }
        t = new Timer();
    }

    /**
     * Shows the intermediate screen.
     */
    public void showIntermediateScreen(int delay, QuestionScreenCtrl thisCtrl) {
        if (t != null) {
            t.cancel();
            t.purge();
            t = null;
        }
        if (nextRoundTimer != null) {
            nextRoundTimer.cancel();
            nextRoundTimer.purge();
        }

        this.intermediateScreenTimer = new Timer();
        this.intermediateScreenTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (roundCounter == 20) {
                            updateServerScore();
                            if (multiplayer) {
                                resetGame();
                                mainCtrl.showIGLeaderboard(true);
                            } else {
                                resetGame();
                                mainCtrl.showLeaderboard(true);
                            }
                            if (intermediateScreenTimer != null) {
                                intermediateScreenTimer.cancel();
                                intermediateScreenTimer.purge();
                            }
                        }
                        else if (multiplayer && roundCounter == 11) {
                            updateServerScore();
                            mainCtrl.showIGLeaderboard(false);
                            returnAfterLeaderboard(thisCtrl);
                            intermediateScreenTimer.cancel();
                            intermediateScreenTimer.purge();
                        }
                        else {
                            if (!multiplayer) {
                                server.nextRound(mainCtrl.gameId);
                            }
                            Question question = server.getNextQuestion(mainCtrl.gameId);
                            mainCtrl.returnGame(thisCtrl, question);
                            nextRoundTimer = new Timer();
                            gauge.clearAreas();
                            passedTime = 0.0d;
                            maxTime = 10.0d;
                        }
                    }
                });
            }
        }, delay);
    }

    private void returnAfterLeaderboard(QuestionScreenCtrl ctrl) {
        returnTimer = new Timer();
        returnTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Question question = server.getNextQuestion(mainCtrl.gameId);
                        mainCtrl.returnGame(ctrl, question);
                        nextRoundTimer = new Timer();
                        gauge.clearAreas();
                        t = new Timer();
                        passedTime = 0.0d;
                        maxTime = 10.0d;
                        nextRound(ctrl, question);
                    }
                });
            }
        }, 10000);
    }

    public abstract void nextRound(QuestionScreenCtrl thisCtrl, Question question);

    protected void nextRoundChecks() {
        if (t != null) {
            t.cancel();
            t.purge();
        }
        count = 0;
        if (this.intermediateScreenTimer != null) {
            this.intermediateScreenTimer.cancel();
            this.intermediateScreenTimer.purge();
        }

        if (roundCounter == 0) {
            delay = 0;
            period = 10000;
            t.cancel();
            t.purge();
        }
    }

    public void prepareNextRound() {
        roundCounter++;
        setRoundCounterLabel();
        setPlayerPointsLabel();
        if (roundCounter > 21 || roundCounter < 0) {
            mainCtrl.showLeaderboard(true);
            resetGame();
            maxTime = 10.0d;
            passedTime = 0.0d;
            return;
        }
        gauge.setVisible(false); //MW: without this the previous gauge that ended will still be visible under the new one
        gauge = new Gauge();
        gauge.setSkin(new FlatSkin(gauge));
        gauge.setUnit("seconds");
        gauge.setMaxValue(10);
        countdown.getChildren().add(gauge);
        maxTime = 10.0d;
        passedTime = 0.0d;
        reactionTable.scrollTo(reactionTable.getItems().size());
        t = new Timer();
        count = 0;
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                count++;
                if (count > 20000) {
                    t.cancel();
                    t.purge();
                    maxTime = 10.0d;
                    passedTime = 0.0d;
                    return;
                }
                passedTime += 0.1d;
                Platform.runLater(new Runnable() { //MW: if this gets deleted it won't work, you can comment it out to see what hapns
                    @Override
                    public void run() {
                        gauge.setValue(maxTime - passedTime);
                    }
                });
            }
        }, 0, 100);
        updateServerScore();
    }

    /**
     * Resets the game status so the next time the player plays, it'll be a new game.
     */
    public void resetGame() {
        gauge.stop();
        count = 120;
        if (t != null) {
            t.cancel();
            t.purge();
        }
        t = null;
        maxTime = 10.0d;
        passedTime = 0.0d;
        nextRoundTimer.cancel();
        nextRoundTimer.purge();
        nextRoundTimer = null;
        if (intermediateScreenTimer != null) {
            intermediateScreenTimer.cancel();
            intermediateScreenTimer.purge();
        }
        intermediateScreenTimer = null;
        if (!multiplayer) {
            server.removeGame(mainCtrl.gameId);
            player.score = 0;
            updateServerScore();
        }

        reactionTable.setVisible(true);
        reactionBar.setVisible(true);
        reactionTable.getItems().clear();
        reactionTable.getItems().add("");
        for (int i = 0; i < 21; i++) {
            reactionTable.getItems().add(" ");
        }
    }

    /**
     * Called when the player presses the 'Nuclear' button.
     * Prompts the user to abort the game and return to the homescreen.
     */
    public void quit() {
        if (mainCtrl.returnHome()) {
            resetGame();
        }
    }

    /**
     * Set all jokers visible again at the start of a game.
     */
    public void resetJokers() {
        joker1.setVisible(true);
        joker2.setVisible(true);
        joker3.setVisible(true);
    }

    public void registerForMessages() {
        Runnable task = () -> {
            Platform.runLater(() -> {
                try {
                    server.registerforMessages(linkforMessages("/topic/comment/", player.gameId), q -> {
                        reactionTable.getItems().add(q.getName() + ":");
                        reactionTable.getItems().add(q.getText());
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
    }

    public void enableMpElements() {
        reactionTable.setVisible(true);
        reactionBar.setVisible(true);
    }
}
