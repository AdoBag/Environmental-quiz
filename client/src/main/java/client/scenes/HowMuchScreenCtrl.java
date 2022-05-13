package client.scenes;

import client.utils.ServerUtils;
import commons.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

import javax.inject.Inject;
import java.net.URL;
import java.util.*;

public class HowMuchScreenCtrl extends QuestionScreenCtrl implements Initializable {
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
    private ImageView answerImageMid;

    @FXML
    private Label activityTitle;

    @FXML
    private Label titlesLabel;

    private String wrongAnswer1;
    private String wrongAnswer2;
    private String correctAnswer;

    private int indexClicked;               // Saves the selected index and is equal to -1 otherwise

    private ArrayList<Label> answerLabels;
    private ArrayList<RadioButton> answerButtonList;

    private int indx;

    private HowMuchQuestion howMuchQuestion;

    @Inject
    public HowMuchScreenCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        answerLabel1.setWrapText(true);
        answerLabel2.setWrapText(true);
        answerLabel3.setWrapText(true);

        joker1.setBackground(Background.EMPTY);
        joker2.setBackground(Background.EMPTY);
        joker3.setBackground(Background.EMPTY);

        this.answerLabels = new ArrayList<>(List.of(answerLabel1, answerLabel2, answerLabel3));
        this.answerButtonList = new ArrayList<>(List.of(answerButton1, answerButton2, answerButton3));

        for (Label l : answerLabels) {
            l.setText("");
        }
    }

    public void displayQuestion(Question question) {
        HowMuchQuestion question1 = (HowMuchQuestion) question;

        questionLabel.setText(question1.getQuestion());
        activityTitle.setText(question1.correctAnswer.getTitle());

        ArrayList<Label> anslabels = new ArrayList<>(List.of(answerLabel1, answerLabel2, answerLabel3));
        Random rand = new Random();
        indx = rand.nextInt(3);
        this.howMuchQuestion = question1;
        setAnswers(indx, question1, anslabels);
    }

    public void setAnswers(int indx, HowMuchQuestion question, ArrayList<Label> anslabels) {
        Activity correct = question.correctAnswer;
        Activity wrong1 = question.wrongAnswer1;
        Activity wrong2 = question.wrongAnswer2;

        this.correctAnswer = correct.getConsumption() + " wH";
        this.wrongAnswer1 = wrong1.getConsumption() + " wH";
        this.wrongAnswer2 = wrong2.getConsumption() + " wH";

        resetAnswers();

        anslabels.get(indx).setText(correct.getConsumption() + " wH");
        if (indx == 0) {
            anslabels.get(1).setText(wrong1.getConsumption() + " wH");
            anslabels.get(2).setText(wrong2.getConsumption() + " wH");
        }
        else if (indx == 1) {
            anslabels.get(0).setText(wrong1.getConsumption() + " wH");
            anslabels.get(2).setText(wrong2.getConsumption() + " wH");
        }
        else if (indx == 2) {
            anslabels.get(0).setText(wrong1.getConsumption() + " wH");
            anslabels.get(1).setText(wrong2.getConsumption() + " wH");
        }

        Image im = decodeImg(correct.getImage());
        answerImageMid.setImage(im);
        centerImage();
    }

    public void centerImage() {
        Image img = answerImageMid.getImage();
        if (img != null) {
            double w = 0;
            double h = 0;
            double ratioX = answerImageMid.getFitWidth() / img.getWidth();
            double ratioY = answerImageMid.getFitHeight() / img.getHeight();
            double reducCoeff = 0;
            if (ratioX >= ratioY) {
                reducCoeff = ratioY;
            } else {
                reducCoeff = ratioX;
            }

            w = img.getWidth() * reducCoeff;
            h = img.getHeight() * reducCoeff;

            answerImageMid.setX((answerImageMid.getFitWidth() - w) / 2);
            answerImageMid.setY((answerImageMid.getFitHeight() - h) / 2);

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
        nextRoundTimer.schedule(new TimerTask() { //MW: starts a new task for the timer
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String correctConsumption = String.valueOf(howMuchQuestion.correctAnswer.consumption_in_wh);
                        showIntermediateScreen(4000, correctConsumption, thisCtrl);
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
     * Returns true if the answer clicked the correct answer.
     * @param index index of the question
     * @return true if the answer clicked was correct
     */
    public boolean wasCorrect(int index) {
        if (index == -1) return false;
        Activity correct = this.howMuchQuestion.correctAnswer;
        this.correctAnswer = correct.getTitle();
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
        server.send(linkforMessages("/topic/comment/", player.gameId), makeComment(mainCtrl.nickname, "Removed a wrong answer"));
        scrollTo();
        for (int i = 0; i < answerLabels.size(); i++) {
            if (answerLabels.get(i).getText().equals(this.wrongAnswer1)) {
                answerLabels.get(i).setText("");
                answerButtonList.get(i).setVisible(false);
                answerButtonList.get(i).setSelected(false);
            }
            joker1.setVisible(false);
        }
    }

    /**
     * Doubles the player's point for one round.
     */
    public void doublePoints() {
        server.send(linkforMessages("/topic/comment/", player.gameId),makeComment(mainCtrl.nickname, "Used double points"));
        scrollTo();
        pointMultiplier = 2;
        joker2.setVisible(false);
    }

    public void showTitles() {
        server.send(linkforMessages("/topic/comment/", player.gameId),makeComment(mainCtrl.nickname, "Saw the titles"));
        scrollTo();
        titlesLabel.setText("Titles of the not shown activities: " + howMuchQuestion.wrongAnswer1.getTitle()
                + "; " + howMuchQuestion.wrongAnswer2.getTitle());
        joker3.setVisible(false);
        titlesLabel.setVisible(true);
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
                answerImageMid.setVisible(true);
            }
        }
        for (Label answer : answerLabels) {
            answer.setStyle("-fx-background-color:null;");
        }
        titlesLabel.setVisible(false);
        indexClicked = -1;
        pointMultiplier = 1;
    }
}
