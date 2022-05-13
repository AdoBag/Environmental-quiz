package commons;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class GuessQuestionTest {
    private Activity a1 = new Activity("1", "path", "cooking a meal", 42, "Imagination");
    private Activity a2 = new Activity("2", "path", "heating a house for 1 hour", 1100, "Imagination");
    private HashSet<Activity> activities = new HashSet<>(3);

    @Test
    void constructorTest() {
        activities.add(a1);
        GuessQuestion guessQuestion = new GuessQuestion(activities);
        assertNotNull(guessQuestion);
    }

    @Test
    void getQuestionTest() {
        activities.add(a1);
        GuessQuestion guessQuestion = new GuessQuestion(activities);
        assertEquals(guessQuestion.getQuestion(), "How much energy does it take?");
    }

    @Test
    void equalsTest() {
        activities.add(a1);
        GuessQuestion guessQuestion = new GuessQuestion(activities);
        GuessQuestion guessQuestion1 = new GuessQuestion(activities);
        assertEquals(guessQuestion, guessQuestion1);
    }

    @Test
    void notEqualsTest() {
        activities.add(a1);
        activities.add(a2);
        HashSet<Activity> activities2 = new HashSet<>(1);
        activities2.add(a1);
        GuessQuestion q1 = new GuessQuestion(activities);
        GuessQuestion q2 = new GuessQuestion(activities2);
        assertNotEquals(q1, q2);
    }

    @Test
    void hashCodeTest() {
        activities.add(a1);
        activities.add(a2);
        GuessQuestion q1 = new GuessQuestion(activities);
        GuessQuestion q2 = new GuessQuestion(activities);
        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    void notEqualHashCodeTest() {
        activities.add(a1);
        activities.add(a2);
        HashSet<Activity> activities2 = new HashSet<>(1);
        activities2.add(a1);
        GuessQuestion q1 = new GuessQuestion(activities);
        GuessQuestion q2 = new GuessQuestion(activities2);
        assertNotEquals(q1, q2);
        assertNotEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void hasToString() {
        activities.add(a1);
        var actual = new GuessQuestion(activities).toString();
        assertTrue(actual.contains(GuessQuestion.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("answers"));
    }

}
