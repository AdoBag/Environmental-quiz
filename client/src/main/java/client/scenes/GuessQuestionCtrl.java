package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;

import java.net.URL;
import java.util.*;


public class GuessQuestionCtrl extends QuestionScreenCtrl implements Initializable {
    @FXML
    private Button answerButton1;

    @FXML
    private Label answerLabel1;

    @FXML
    private Label questionLabel;

    @FXML
    private ImageView answerImage;

    @FXML
    private ImageView jokerImage;

    @FXML
    private TextField answerField;

    @FXML
    private Label digitCountLabel;

    @FXML
    private Label firstDigitLabel;

    private Long correctAnswer;
    private Long enteredAnswer;

    private GuessQuestion guessQuestion;

    @Inject
    public GuessQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initializes the scene.
     * @param location the URL of the scene
     * @param resources other resources used for the scene
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        answerLabel1.setWrapText(true);

        answerButton1.setContentDisplay(ContentDisplay.CENTER);

        joker1.setBackground(Background.EMPTY);
        joker2.setBackground(Background.EMPTY);
        joker3.setBackground(Background.EMPTY);

        // force the field to be numeric only
        answerField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    answerField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

    }

    /**
     * Starts a new game.
     * @param multiplayer boolean that is true if this game is MP
     * @param thisCtrl this Controller
     * @param question the question to be displayed
     */
    public void startGame(boolean multiplayer, QuestionScreenCtrl thisCtrl, Question question) {
        super.startGame(multiplayer, thisCtrl, question);
        resetAnswers();
        nextRound(thisCtrl, question);
    }

    /**
     * Shows the intermediate screen.
     * @param delay the delay of a timer
     * @param correctTitle the title of the correct activity
     * @param thisCtrl this controller
     */
    public void showIntermediateScreen(int delay, QuestionScreenCtrl thisCtrl) {
        if (enteredAnswer != null && enteredAnswer.equals(correctAnswer)) {
            mainCtrl.showCorrect(player.calculateGuessScore(correctAnswer, enteredAnswer, selectedTime, maxTime, pointMultiplier));
        } else {
            mainCtrl.showGuessIncorrect(correctAnswer, player.calculateGuessScore(correctAnswer, enteredAnswer,
                    selectedTime, maxTime, pointMultiplier));
        }
        super.showIntermediateScreen(delay, thisCtrl);
    }

    /**
     * A timer thread for each round so each round takes a fixed amount of seconds.
     * [an instance of a "intermediateScreen" should be added later]
     */
    @Override
    public void nextRound(QuestionScreenCtrl thisCtrl, Question question) {
        nextRoundChecks();
        super.prepareNextRound();
        displayQuestion(question);
        nextRoundTimer = new Timer();
        nextRoundTimer.schedule(new TimerTask() { //MW: starts a new task for the timer
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        showIntermediateScreen(4000, thisCtrl);
                    }
                });
            }
        }, 10000);
    }

    @Override
    public void transferData(QuestionScreenCtrl other) {
        super.transferData(other);
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
     * Displays the question.
     */
    public void displayQuestion(Question question) {
        GuessQuestion question1 = (GuessQuestion) question;
        questionLabel.setText(question1.getQuestion());

        setAnswers(question1);

        this.guessQuestion = question1;
    }


    /**
     * Called from displayQuestion. Sets the answer label and the image.
     * @param question ComparisonQuestion object
     */
    public void setAnswers(GuessQuestion question) {
        resetAnswers();
        Activity correct = question.answer;
        Image correctIm = decodeImg(correct.getImage());
        answerLabel1.setText(correct.getTitle());
        correctAnswer = correct.getConsumption();
        answerImage.setImage(correctIm);
        centerImage();
    }

    /**
     * Centers the image on the imageView.
     */
    public void centerImage() {
        Image img = answerImage.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;
            double ratioX = answerImage.getFitWidth() / img.getWidth();
            double ratioY = answerImage.getFitHeight() / img.getHeight();
            double reducCoeff = 0;
            if (ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }
            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;
            answerImage.setX((answerImage.getFitWidth() - w) / 2);
            answerImage.setY((answerImage.getFitHeight() - h) / 2);
        }
    }

    /**
     * Activates when answer button is clicked.
     */
    public void answerClicked() {
        selectedTime = passedTime;
        try {
            enteredAnswer = Long.parseLong(answerField.getText());
        }
        catch (NumberFormatException e) {
            answerField.setStyle("-fx-background-color: transparent");
            return;
        }
        answerField.setStyle("-fx-background-color: #fffa8b;");
    }

    /**
     * joker1: remove wrong answer.
     * removes a random wrong answer and also removes the joker
     */
    public void showNumberOfDigits() {
        server.send(linkforMessages("/topic/comment/", player.gameId), makeComment(mainCtrl.nickname, "Saw the digit count"));
        scrollTo();
        int numberOfDigits = (int) (Math.log10(correctAnswer) + 1);
        digitCountLabel.setText("Number of digits: " + numberOfDigits);
        digitCountLabel.setVisible(true);
        joker1.setVisible(false);
    }

    /**
     * Doubles the player's point for one round.
     */
    public void doublePoints() {
        server.send(linkforMessages("/topic/comment/", player.gameId), makeComment(mainCtrl.nickname, "Used double points"));
        scrollTo();
        pointMultiplier = 2;
        joker2.setVisible(false);
    }

    public void showFirstDigit() {
        server.send(linkforMessages("/topic/comment/", player.gameId),makeComment(mainCtrl.nickname, "Saw the first digit"));
        scrollTo();
        firstDigitLabel.setText("First digit: " + Long.toString(correctAnswer).substring(0, 1));
        firstDigitLabel.setVisible(true);
        joker3.setVisible(false);
    }

    /**
     * Resets the possibly hidden answer (in case joker was used).
     * also resets color of correct answer to clear.
     */
    public void resetAnswers() {
        enteredAnswer = null;
        answerField.setStyle("-fx-background-color:null;");
        digitCountLabel.setVisible(false);
        firstDigitLabel.setVisible(false);
        answerField.clear();
        pointMultiplier = 1;
    }

    /**
     * Deals with the user's keyboard input.
     * @param e the key that was pressed.
     */
    public void keyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            answerClicked();
        }
    }
}
