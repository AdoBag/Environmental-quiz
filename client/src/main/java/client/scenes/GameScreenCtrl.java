package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

import java.net.URL;
import java.util.*;


public class GameScreenCtrl extends QuestionScreenCtrl implements Initializable {
    @FXML
    private ToggleGroup answerButtons;

    @FXML
    private RadioButton answerButton1;

    @FXML
    private RadioButton answerButton2;

    @FXML
    private RadioButton answerButton3;

    @FXML
    private Label answerLabel1;

    @FXML
    private Label answerLabel2;

    @FXML
    private Label answerLabel3;

    @FXML
    private Label questionLabel;

    @FXML
    private ImageView answerImageLeft;

    @FXML
    private ImageView answerImageMid;

    @FXML
    private ImageView answerImageRight;

    @FXML
    private ImageView jokerImage;

    @FXML
    private Label whLabel;

    private Activity wrong1;
    private Activity wrong2;

    private ArrayList<Label> answerLabels;
    private ArrayList<RadioButton> answerButtonList;
    private ArrayList<ImageView> imageList;

    private int indx;
    private int indexClicked;

    private ComparisonQuestion comparisonQuestion;

    @Inject
    public GameScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
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
        answerLabel2.setWrapText(true);
        answerLabel3.setWrapText(true);

        answerButton1.setContentDisplay(ContentDisplay.CENTER);
        answerButton2.setContentDisplay(ContentDisplay.CENTER);
        answerButton3.setContentDisplay(ContentDisplay.CENTER);

        joker1.setBackground(Background.EMPTY);
        joker2.setBackground(Background.EMPTY);
        joker3.setBackground(Background.EMPTY);

        this.answerLabels = new ArrayList<>(List.of(answerLabel1, answerLabel2, answerLabel3));
        this.answerButtonList = new ArrayList<>(List.of(answerButton1, answerButton2, answerButton3));
        this.imageList = new ArrayList<>(List.of(answerImageLeft, answerImageMid, answerImageRight));
        for (Label l : answerLabels) {
            l.setText("");
        }
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
    public void showIntermediateScreen(int delay, String correctTitle, QuestionScreenCtrl thisCtrl) {
        boolean isCorrect = wasCorrect(indexClicked);
        if (isCorrect) {
            mainCtrl.showCorrect(player.calculateScore(wasCorrect(indexClicked), selectedTime, maxTime, pointMultiplier));
        } else {
            mainCtrl.showIncorrect(correctTitle);
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
        prepareNextRound();
        displayQuestion(question);
        nextRoundTimer = new Timer();
        nextRoundTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        showIntermediateScreen(4000, comparisonQuestion.correctAnswer.title, thisCtrl);
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
     * Takes a ComparisonQuestion object as a parameter, sets the question field, creates a random 0-2.
     * index for the correct answer so that right/wrong answers aren't always in the same place.
     */
    public void displayQuestion(Question question) {
        ComparisonQuestion question1 = (ComparisonQuestion) question;
        questionLabel.setText(question1.getQuestion());

        Random rand = new Random();
        indx = rand.nextInt(3);

        setAnswers(indx, question1);

        this.comparisonQuestion = question1;
    }


    /**
     * Called from displayQuestion. Sets answer fields and their respective images, according to the given index.
     * @param indx The index of the correct answer in AnswerLabels
     * @param question ComparisonQuestion object
     */
    public void setAnswers(int indx, ComparisonQuestion question) {
        Activity correct = question.correctAnswer;

        Iterator<Activity> itr = question.wrongAnswers.iterator();
        wrong1 = itr.next();
        wrong2 = itr.next();

        resetAnswers();

        Image correctIm = decodeImg(correct.getImage());
        Image wrong1Im = decodeImg(wrong1.getImage());
        Image wrong2Im = decodeImg(wrong2.getImage());
        Image[] s = new Image[5];
        s[0] = correctIm;
        s[1] = wrong1Im;
        s[2] = wrong2Im;

        answerLabels.get(indx).setText(String.valueOf(correct.getTitle()));
        if (indx == 0) {
            answerLabels.get(indx + 1).setText(String.valueOf(wrong1.getTitle()));
            answerLabels.get(indx + 2).setText(String.valueOf(wrong2.getTitle()));

            answerImageLeft.setImage(correctIm);
            answerImageMid.setImage(wrong1Im);
            answerImageRight.setImage(wrong2Im);
        }
        else if (indx == 1) {
            answerLabels.get(0).setText(String.valueOf(wrong1.getTitle()));
            answerLabels.get(0).setText(String.valueOf(wrong2.getTitle()));

            answerImageLeft.setImage(wrong1Im);
            answerImageMid.setImage(correctIm);
            answerImageRight.setImage(wrong2Im);
        }
        else if (indx == 2) {
            answerLabels.get(0).setText(String.valueOf(wrong1.getTitle()));
            answerLabels.get(1).setText(String.valueOf(wrong2.getTitle()));

            answerImageLeft.setImage(wrong1Im);
            answerImageMid.setImage(wrong2Im);
            answerImageRight.setImage(correctIm);
        }
    }

    /**
     * Returns true if the answer clicked the correct answer.
     * @param index index of the question
     * @return true if the answer clicked was correct
     */
    public boolean wasCorrect(int index) {
        if (index == -1) return false;
        if (index == indx) {
            return true;
        }
        return false;
    }

    /**
     * Activates when answer 1 is clicked, changes the background color of the answer.
     * and checks if the answer is correct
     */
    public void answer1Clicked() {
        selectedTime = passedTime;
        wasCorrect(0);
        answerLabel1.setStyle("-fx-background-color: #fffa8b;");
        answerLabel2.setStyle("");
        answerLabel3.setStyle("");
        this.indexClicked = 0;
    }

    /**
     * Activates when answer 2 is clicked, changes the background color of the answer.
     * and checks if the answer is correct
     */
    public void answer2Clicked() {
        selectedTime = passedTime;
        wasCorrect(1);
        answerLabel2.setStyle("-fx-background-color: #fffa8b;");
        answerLabel1.setStyle("");
        answerLabel3.setStyle("");
        this.indexClicked = 1;
    }

    /**
     * Activates when answer 3 is clicked, changes the background color of the answer.
     * and checks if the answer is correct
     */
    public void answer3Clicked() {
        selectedTime = passedTime;
        wasCorrect(2);
        answerLabel3.setStyle("-fx-background-color: #fffa8b");
        answerLabel2.setStyle("");
        answerLabel1.setStyle("");
        this.indexClicked = 2;
    }


    /**
     * joker1: remove wrong answer.
     * removes a random wrong answer and also removes the joker
     */
    public void removeWrongAnswer() {
        server.send(linkforMessages("/topic/comment/", player.gameId), makeComment(mainCtrl.nickname, "Used remove one wrong answer"));
        scrollTo();
        for (int i = 0; i < answerLabels.size(); i++) {
            if (answerLabels.get(i).getText().equals(this.wrong1.getTitle())) {
                answerLabels.get(i).setText("");
                answerButtonList.get(i).setVisible(false);
                answerButtonList.get(i).setSelected(false);
                imageList.get(i).setVisible(false);
            }
            joker1.setVisible(false);
        }
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

    public void showWh() {
        server.send(linkforMessages("/topic/comment/", player.gameId), makeComment(mainCtrl.nickname, "Saw the consumptions"));
        scrollTo();
        whLabel.setText("The Wh of the activities (in random order): " + comparisonQuestion.correctAnswer.consumption_in_wh
                + ", " + wrong1.consumption_in_wh + ", " + wrong2.consumption_in_wh);
        whLabel.setVisible(true);
        joker3.setVisible(false);
    }

    /**
     * Resets the possibly hidden answer (in case joker was used).
     * also resets color of correct answer to clear.
     */
    public void resetAnswers() {
        for (int i = 0; i < answerButtonList.size(); i++) {
            answerButtonList.get(i).setSelected(false);
            if (!answerButtonList.get(i).isVisible()) {
                answerButtonList.get(i).setVisible(true);
                imageList.get(i).setVisible(true);
            }
        }
        for (Label answer : answerLabels) {
            answer.setStyle("-fx-background-color:null;");
        }
        whLabel.setVisible(false);
        indexClicked = -1;
        pointMultiplier = 1;
    }
}
