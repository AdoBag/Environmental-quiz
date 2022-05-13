package commons;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashSet;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@JsonTypeName("comparisonquestion")
public class ComparisonQuestion extends Question implements Serializable {
    public HashSet<Activity> wrongAnswers;
    public Activity correctAnswer;
    public String question;


    public ComparisonQuestion() {
        super();
    }

    /**
     * Constructor for ComparisonQuestion.
     * @param answers - the list with answers
     */
    public ComparisonQuestion(HashSet<Activity> answers) {
        super(answers);
        getWrongAnswers(getCorrectAnswer(answers),answers);
    }

    /**
     * Simple method that return the question that needs to be shown to users.
     * @return - returns string with the question
     */
    public String getQuestion() {
        return "What requires more energy?";
    }

    /**
     * Finds the activity with the highest consumption (so the correct answer).
     * @return - returns the activity with the highest consumption
     */
    public Activity getCorrectAnswer(HashSet<Activity> answer) {
        Activity highest = null;
        long highestConsumption = 0;
        for (Activity possibleAnswer : answer) {
            if (possibleAnswer.getConsumption() > highestConsumption) {
                highest = possibleAnswer;
                highestConsumption = highest.getConsumption();
            }
        }
        this.correctAnswer = highest;
        return highest;
    }

    /**
     * Iterates over the answers and adds the incorrect ones to the set of contaning them.
     * @param correctAnswer - the answer that should not be part of the wrongAnswer set
     * @return - returns a set containing all the wrong (so not highest consumption) answers from the original set.
     */
    public HashSet<Activity> getWrongAnswers(Activity correctAnswer, HashSet<Activity> answer) {
        HashSet<Activity> wrongAnswers = new HashSet<>(2);
        for (Activity possibleAnswer : answer) {
            if (!possibleAnswer.equals(correctAnswer)) {
                wrongAnswers.add(possibleAnswer);
            }
        }
        this.wrongAnswers = wrongAnswers;
        return wrongAnswers;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
