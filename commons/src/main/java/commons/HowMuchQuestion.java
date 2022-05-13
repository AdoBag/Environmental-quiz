package commons;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashSet;
import java.util.Iterator;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;


@JsonTypeName("howmuchquestion")
public class HowMuchQuestion extends Question {
    public Activity correctAnswer;
    public Activity wrongAnswer1;
    public Activity wrongAnswer2;
    public String question;

    public HowMuchQuestion() {
        super();
    }

    /**
     * Constructor of HomMuchQuestion.
     * @param answers - list of answers
     */
    public HowMuchQuestion(HashSet<Activity> answers) {
        super(answers);
        setAllAnswers();
    }

    /**
     * Returns the question that needs to be shown to users.
     * @return - returns string with the question
     */
    public String getQuestion() {
        return "How much energy does this consume?";
    }

    /**
     * Iterate through answers HashSet and set the three random activities as correct and wrong answers.
     * (order doesn't matter bc they're random)
     */
    public void setAllAnswers() {
        Iterator<Activity> itr = answers.iterator();
        correctAnswer = itr.next();
        wrongAnswer1 = itr.next();
        wrongAnswer2 = itr.next();
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



