package commons;

import java.util.HashSet;

/**
 * This class extends Question and doesn't add anything. This is purely to allow for testing.
 */
public class TestQuestion extends Question {
    public TestQuestion() {
        super();
    }

    public TestQuestion(HashSet<Activity> answers) {
        super(answers);
    }
}
