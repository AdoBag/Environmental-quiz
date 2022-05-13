/**
 * MW: useless for now but i'll use it when implementing multiple questiontypes.
 * MW: feel free to edit it though!!! just don't delete the comments
 */

package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Lob
    @Column(name = "question", columnDefinition = "BLOB")
    public HashSet<Question> questionList;

    public byte roundCounter;

    protected Game() {
    }

    /**
     * Constructor of the Game.
     * @param questionList - list with questions to be used by this game
     */
    public Game(HashSet<Question> questionList) {
        this.questionList = questionList;
        this.roundCounter = -1;
    }

    /**
     * Increase the roundCounter by 1 until the last round.
     */
    public void nextRound() {
        if (roundCounter == 20) return;
        this.roundCounter++;
    }

    /**
     * Find the question corresponding to the current round.
     * @return - returns the question from the list of questions
     */
    public Question getCurrentQuestion() {
        Iterator<Question> iterator = questionList.iterator();
        Question res = iterator.next();
        for (int i = 0; i < roundCounter; i++) {
            res = iterator.next();
        }
        return res;
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
