package commons;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.HashSet;

/**
 * The children of this class will be the different types of questions.
 * Feel free to edit the fields
 */
@JsonTypeInfo (use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes ({
    @JsonSubTypes.Type(value = ComparisonQuestion.class, name = "comparisonquestion"),
    @JsonSubTypes.Type(value = GuessQuestion.class, name = "guessquestion"),
    @JsonSubTypes.Type(value = HowMuchQuestion.class, name = "howmuchquestion")

    })

public abstract class Question implements Serializable {
    public HashSet<Activity> answers;

    public Question() {
    }

    /**
     * Constructor for the Question.
     * @param answers - list of answers
     */
    public Question(HashSet<Activity> answers) {
        this.answers = answers;
    }
}
