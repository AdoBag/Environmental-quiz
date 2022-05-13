/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import commons.Question;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.stage.StageStyle;

import javafx.stage.WindowEvent;

import javafx.util.Pair;

import java.util.Optional;

public class MainCtrl {

    private Stage primaryStage;
    private Stage secondaryStage = new Stage();

    private AddPlayerCtrl playerCtrl;
    private Scene player;

    private HomeScreenCtrl homeScreenCtrl;
    private Scene home;

    private AdminScreenCtrl adminScreenCtrl;
    private Scene admin;

    private GameScreenCtrl gameScreenCtrl;
    private Scene game;

    private GuessQuestionCtrl guessQuestionCtrl;
    private Scene guess;

    private WaitingRoomScreenCtrl waitingRoomScreenCtrl;
    private Scene waiting;

    private LeaderboardCtrl leaderboardCtrl;
    private Scene leaderboard;

    private CountdownScreenCtrl countdownScreenCtrl;
    private Scene countdown;

    private CorrectAnswerCtrl correctAnswerCtrl;
    private Scene correctAnswer;

    private IncorrectAnswerCtrl incorrectAnswerCtrl;
    private Scene incorretAnswer;

    private HowMuchScreenCtrl howMuchScreenCtrl;
    private Scene howmuch;

    public String nickname;
    public long playerId;
    public long gameId = -1;
    public int games = 0;

    /**
     * Initialises every scene that needs it.
     * @param primaryStage - the primary stage of the app
     * @param play - AddPlayer scene
     * @param home - HomeScreen scene
     * @param admin - AdminScreen scene
     * @param game - GameScreen scene
     * @param waiting - WaitingRoomScreen scene
     * @param leaderboard - Leaderboard scene
     * @param countdown - CountDownScreen scene
     * @param correctAnswer - CorrectAnswer scene
     * @param incorrectAnswer - IncorrectAnswer scene
     */
    public void initialize(Stage primaryStage,
                           Pair<AddPlayerCtrl, Parent> play, Pair<HomeScreenCtrl, Parent> home,
                           Pair<AdminScreenCtrl, Parent> admin, Pair<GameScreenCtrl, Parent> game,
                           Pair<GuessQuestionCtrl, Parent> guess,
                           Pair<WaitingRoomScreenCtrl, Parent> waiting, Pair<LeaderboardCtrl, Parent> leaderboard,
                           Pair<CountdownScreenCtrl, Parent> countdown, Pair<CorrectAnswerCtrl, Parent> correctAnswer,
                           Pair<IncorrectAnswerCtrl, Parent> incorrectAnswer, Pair<HowMuchScreenCtrl, Parent> howmuch) {
        this.primaryStage = primaryStage;

        this.playerCtrl = play.getKey();
        this.player = new Scene(play.getValue());

        this.homeScreenCtrl = home.getKey();
        this.home = new Scene(home.getValue());

        this.adminScreenCtrl = admin.getKey();
        this.admin = new Scene(admin.getValue());

        this.gameScreenCtrl = game.getKey();
        this.game = new Scene(game.getValue());

        this.guessQuestionCtrl = guess.getKey();
        this.guess = new Scene((guess.getValue()));

        this.waitingRoomScreenCtrl = waiting.getKey();
        this.waiting = new Scene(waiting.getValue());

        this.leaderboardCtrl = leaderboard.getKey();
        this.leaderboard = new Scene(leaderboard.getValue());

        this.countdownScreenCtrl = countdown.getKey();
        this.countdown = new Scene(countdown.getValue());

        this.correctAnswerCtrl = correctAnswer.getKey();
        this.correctAnswer = new Scene(correctAnswer.getValue());

        this.incorrectAnswerCtrl = incorrectAnswer.getKey();
        this.incorretAnswer = new Scene(incorrectAnswer.getValue());

        this.howMuchScreenCtrl = howmuch.getKey();
        this.howmuch = new Scene(howmuch.getValue());

        enterPlayer();
        while (nickname == null) {
            secondaryStage.setScene(player);
            secondaryStage.showAndWait();
        }
        showHome();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                if (!quitApp()) {
                    e.consume();
                }
            }
        });
        primaryStage.show();
    }

    /**
     * Sets the screen where player can enter name.
     */
    public void enterPlayer() {
        primaryStage.setScene(home);
        primaryStage.show();
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.initStyle(StageStyle.UNDECORATED);
        secondaryStage.setScene(player);
        secondaryStage.showAndWait();
    }

    /**
     * Shows the Home Screen.
     */
    public void showHome() {
        secondaryStage.close();
        primaryStage.setTitle("QUIZZZ: Main menu");
        primaryStage.setScene(home);
        home.setOnKeyPressed(p -> homeScreenCtrl.keyPressed(p));
    }

    /**
     * Shows the Admin Screen.
     */
    public void showAdmin() {
        primaryStage.setTitle("QUIZZZ: Admin screen");
        primaryStage.setScene(admin);
        admin.setOnKeyPressed(p -> homeScreenCtrl.keyPressed(p));
    }

    /**
     * Returns to game.
     * @param ctrl - the screen to return to
     * @param question - the questionType to show
     */
    public void returnGame(QuestionScreenCtrl ctrl, Question question) {
        primaryStage.setTitle("QUIZZZ: Game");
        String questionType = question.getClass().toString();
        if (questionType.contains("Comparison")) {
            primaryStage.setScene(game);
            gameScreenCtrl.transferData(ctrl);
            gameScreenCtrl.nextRound(gameScreenCtrl, question);
        }
        else if (questionType.contains("Guess")) {
            primaryStage.setScene(guess);
            guessQuestionCtrl.transferData(ctrl);
            guessQuestionCtrl.nextRound(guessQuestionCtrl, question);
        }
        else if (questionType.contains("How")) {
            primaryStage.setScene(howmuch);
            howMuchScreenCtrl.transferData(ctrl);
            howMuchScreenCtrl.nextRound(howMuchScreenCtrl, question);
        }
    }

    /**
     * Shows the GameScreen.
     * @param question - the question type to show
     */
    public void showGame(Question question) {
        primaryStage.setTitle("QUIZZZ: Singleplayer");
        guess.setOnKeyPressed(p -> guessQuestionCtrl.keyPressed(p));
        gameScreenCtrl.registerForMessages();
        guessQuestionCtrl.registerForMessages();
        howMuchScreenCtrl.registerForMessages();
        gameScreenCtrl.hideMultiplayerItems();
        guessQuestionCtrl.hideMultiplayerItems();
        howMuchScreenCtrl.hideMultiplayerItems();
        String questionType = question.getClass().toString();
        if (questionType.contains("Comparison")) {
            primaryStage.setScene(game);
            gameScreenCtrl.startGame(false, gameScreenCtrl, question);
        }
        else if (questionType.contains("Guess")) {
            primaryStage.setScene(guess);
            guessQuestionCtrl.startGame(false, guessQuestionCtrl, question);
        }
        else if (questionType.contains("How")) {
            primaryStage.setScene(howmuch);
            howMuchScreenCtrl.startGame(false, howMuchScreenCtrl, question);
        }
        games++;
    }

    /**
     * Shows the multiplayer screen.
     * @param question - the question type to be shown
     */
    public void showMultiplayer(Question question) {
        primaryStage.setTitle("QUIZZZ: Multiplayer");
        guess.setOnKeyPressed(p -> guessQuestionCtrl.keyPressed(p));
        String questionType = question.getClass().toString();
        if (questionType.contains("Comparison")) {
            primaryStage.setScene(game);
            gameScreenCtrl.startGame(true, gameScreenCtrl, question);
        }
        else if (questionType.contains("Guess")) {
            primaryStage.setScene(guess);
            guessQuestionCtrl.startGame(true, guessQuestionCtrl, question);
        }
        else if (questionType.contains("How")) {
            primaryStage.setScene(howmuch);
            howMuchScreenCtrl.startGame(true, howMuchScreenCtrl, question);
        }
        games++;
    }

    /**
     * Shows the Waiting Room Screen for Multiplayer games.
     */
    public void showWaiting() {
        primaryStage.setTitle("QUIZZZ: Waiting room");
        primaryStage.setScene(waiting);
        waitingRoomScreenCtrl.checkForUpdates();
        waitingRoomScreenCtrl.refresh();
    }

    /**
     * First updates the leaderboard, then shows it and then enables auto updating.
     * @param isEndScreen - true if the leaderboard is the last screen
     */
    public void showLeaderboard(boolean isEndScreen) {
        leaderboardCtrl.setLeaderboard(true, isEndScreen);
        primaryStage.setTitle("QUIZZZ: All time Leaderboard");
        primaryStage.setScene(leaderboard);
    }

    /**
     * Show intermediate leaderboard during the game.
     * @param isEndScreen - true if the leaderboard is the last screen
     */
    public void showIGLeaderboard(boolean isEndScreen) {
        leaderboardCtrl.setLeaderboard(false, isEndScreen);
        primaryStage.setTitle("QUIZZZ: Leaderboard");
        primaryStage.setScene(leaderboard);
    }

    /**
     * Shows the countdown before a game.
     * @param isMultiplayer - true if the countdown is for multiplayer
     */
    public void showCountdown(boolean isMultiplayer) {
        primaryStage.setTitle("QUIZZZ: Ready?");
        primaryStage.setScene(countdown);
        if (!isMultiplayer) {
            gameScreenCtrl.createGame();
        }
        countdownScreenCtrl.showCountdown(isMultiplayer);
    }


    /**
     * Shows the correct answer screen between questions if the answer was correct.
     * @param points - the gained points
     */
    public void showCorrect(int points) {
        primaryStage.setTitle("Congratulations!");
        primaryStage.setScene(correctAnswer);
        correctAnswerCtrl.showCorrect(points);
    }


    /**
     * Shows the incorrect answer screen between questions if the answer was incorrect.
     * @param correctTitle - the title of the correct answer
     */
    public void showIncorrect(String correctTitle) {
        primaryStage.setTitle("Not this time...");
        primaryStage.setScene(incorretAnswer);
        incorrectAnswerCtrl.showIncorrect(correctTitle);
    }

    /**
     * Shows the incorrect answer screen between questions if the answer was incorrect.
     * @param correctUsage - the correct answer
     * @param pointsNo - number of gained points
     */
    public void showGuessIncorrect(long correctUsage, int pointsNo) {
        primaryStage.setTitle("Not this time...");
        primaryStage.setScene(incorretAnswer);
        incorrectAnswerCtrl.showGuessIncorrect(correctUsage, pointsNo);
    }

    /**
     * Returns to primary stage so that the FileChooser could work in the AdminScreen.
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Quits the application.
     * @return - returns true if the player confirmed to quit, false otherwise.
     */
    public boolean quitApp() {
        Alert.AlertType type = Alert.AlertType.CONFIRMATION;
        Alert alert = new Alert(type, "Do you really want to quit the application?");
        alert.getDialogPane().setHeaderText("");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");

        Optional<ButtonType> res = alert.showAndWait();
        if (res.get() == ButtonType.OK) {
            waitingRoomScreenCtrl.decreaseNumber();
            homeScreenCtrl.setPlayerOffline();
            waitingRoomScreenCtrl.stop();
            playerCtrl.stop();
            adminScreenCtrl.stop();
            Platform.exit();
            System.exit(0);
            return true;
        }
        return false;
    }

    /**
     * This method is reused for quit/return home buttons ('Nuclear' button).
     * Shows the confirmation alert and asks the player if they want to return to the homescreen
     * @return - returns true if player wants to go back and false otherwise
     */
    public boolean returnHome() {
        Alert.AlertType type = Alert.AlertType.CONFIRMATION;
        Alert alert = new Alert(type, "Do you really want to return to the homescreen?");
        alert.getDialogPane().setHeaderText("");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");
        Optional<ButtonType> res = alert.showAndWait();
        if (res.get() == ButtonType.OK) {

            showHome();
            gameScreenCtrl.resetJokers();
            guessQuestionCtrl.resetJokers();
            howMuchScreenCtrl.resetJokers();
            gameScreenCtrl.enableMpElements();
            guessQuestionCtrl.enableMpElements();
            howMuchScreenCtrl.enableMpElements();
            return true;
        }
        return false;
    }

    /**
     * Load images for HomeScreen and AdminScreen.
     * @param background - imageview for background image
     * @param nukeSymbol - imageview containing the nuke
     * @param quitButton - button which will be used for quitting
     */
    public void loadImages(ImageView background, ImageView nukeSymbol, Button quitButton) {
        loadQuitButton(nukeSymbol, quitButton);
    }

    /**.
     * load quitButton image for all screens.
     * @param nukeSymbol - imageview containing the nuke
     * @param quitButton - button which will be used for quitting
     */
    public void loadQuitButton(ImageView nukeSymbol, Button quitButton) {
        Image nukeImage = new Image("https://i.ibb.co/t3mQ7YS/nuclear.png");
        nukeSymbol.setImage(nukeImage);
        quitButton.setBackground(Background.EMPTY);
    }

    /**
     * Starts the countdown if the user wants to start the game.
     * @param isMultiplayer - true for a multiplayer game and false otherwise
     */
    public void startConfirmation(boolean isMultiplayer) {
        Alert.AlertType type = Alert.AlertType.CONFIRMATION;
        Alert alert = new Alert(type, "Are you ready to start?");
        alert.getDialogPane().setHeaderText("");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");

        Optional<ButtonType> res = alert.showAndWait();
        if (res.get() == ButtonType.OK) {
            showCountdown(isMultiplayer);
        }
    }

    /**
     * Quitter the intermediate leaderboard.
     */
    public void quitIntermediateLb() {
        gameScreenCtrl.returnTimer.cancel();
        gameScreenCtrl.returnTimer.purge();
        gameScreenCtrl.resetGame();
        guessQuestionCtrl.returnTimer.cancel();
        guessQuestionCtrl.returnTimer.purge();
        guessQuestionCtrl.resetGame();
        howMuchScreenCtrl.returnTimer.cancel();
        howMuchScreenCtrl.returnTimer.purge();
        howMuchScreenCtrl.resetGame();
    }
}