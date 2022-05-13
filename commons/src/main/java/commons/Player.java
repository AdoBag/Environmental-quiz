package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    public String nickname;
    public int score;
    public int highscore;
    public long gameId;
    public boolean isOnline;
    public boolean isWaitingroom;

    private Player() {}

    /**
     * Constructor for Player object.
     * @param name - the player nickname that user provides in Login screen
     */
    public Player(String name) {
        this.nickname = name;
        this.score = 0;
        this.highscore = 0;
        this.gameId = -1;
        this.isOnline = true;
        this.isWaitingroom = false;
    }

    /**
     * Getter for the nickname.
     * @return - returns the nickname
     */
    public String getNickname() {
        return nickname;
    }

    public Player(String name, int score, int highscore, long gameId, boolean isOnline) {
        this.nickname = name;
        this.score = score;
        this.highscore = highscore;
        this.gameId = gameId;
        this.isOnline = isOnline;
        this.isWaitingroom = false;
    }

    /**
     * Calculates a player's score for a question given some parameters.
     * @param correctAnswer - indicates whether a player answered correctly
     * @param selectedTime - how many seconds passed after the answer was selected
     * @param maxTime - round length in seconds
     * @return - returns the number of points added to the player's score
     */
    public int calculateScore(boolean correctAnswer, Double selectedTime, Double maxTime, int pointMultiplier) {
        int points = 0;
        if (correctAnswer) {
            if (maxTime == null || selectedTime == null) return points;
            points = (int) Math.ceil((maxTime - selectedTime) * 10) * pointMultiplier;
            if (points > 0 && points <= 200) { /** AB: don't ask */
                score += points;
            }
            else {
                points = 0;
            }

        }
        return points;
    }

    /**
     * Calculates a player's score for a guess question given some parameters.
     * @param correctAnswer - the energy usage of the activity
     * @param answer - the player's guess for the energy usage of the activity
     * @param selectedTime - how many seconds passed after the answer was selected
     * @param maxTime - round length in seconds
     * @param multiplier - a number which will multiply the score
     * @return - returns the number of points added to the player's score
     */
    public int calculateGuessScore(Long correctAnswer, Long answer, Double selectedTime, Double maxTime, int multiplier) {
        int points = 0;
        if (maxTime == null || selectedTime == null || answer == null) return points;
        double ratio;
        if (answer > correctAnswer) ratio = correctAnswer * 1.0 / answer;
        else ratio = answer / correctAnswer;
        points = (int) Math.ceil((maxTime - selectedTime) * 10) * multiplier;
        points *= Math.ceil(ratio * 100) / 100;
        if (points > 0 && points <= 200) {
            score += points;
        }
        else {
            points = 0;
        }
        return points;
    }

    /**
     * Getter for the id.
     * @return - returns the id
     */
    public long getId() {
        return id;
    }

    /**
     * Getter for the score.
     * @return - returns the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Setter for the score.
     * @param score - the score to be set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Getter for the highscore.
     * @return - returns the highscore
     */
    public int getHighscore() {
        return highscore;
    }

    /**
     * Setter for the highscore.
     * @param highscore - the highscore to be set
     */
    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }


    /**
     * Whenever the current score is higher than the Highscore, the Highscore gets updated.
     */
    public void updateScore() {
        if (score > highscore)
            highscore = score;
    }

    /**
     * Set waitingroom status to true or false.
     * @param wait - true if in waiting room, false if not.
     */
    public void setWaitingroom(boolean wait) {
        isWaitingroom = wait;
    }
}
