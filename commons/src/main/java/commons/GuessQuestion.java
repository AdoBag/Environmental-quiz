package commons;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashSet;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@JsonTypeName("guessquestion")
public class GuessQuestion extends Question implements Serializable {
    public String question;
    public Activity answer;

    public GuessQuestion() {
        super();
    }

    /**
     * Constructor for the GuessQuestion.
     * @param answers - list with answers
     */
    public GuessQuestion(HashSet<Activity> answers) {
        super(answers);
        answer = answers.iterator().next();
    }

    /**
     * Simple method that returns the question that needs to be shown to users.
     * @return - returns string with the question
     */
    public String getQuestion() {
        return "How much energy does it take?";
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