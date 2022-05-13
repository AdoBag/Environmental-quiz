package commons;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This still needs work, since there weren't any methods in the Question class yet.
 */
class QuestionTest {
    @Test
    void constructorTest() {
        HashSet<Activity> answers = new HashSet<>(1);
        Activity activity = new Activity("3", "aPath", "title", 301, "imagination");
        answers.add(activity);
        TestQuestion question = new TestQuestion(answers);
        assertNotNull(question);
        assertEquals(answers, question.answers);
    }
}