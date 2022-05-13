package commons;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HowMuchQTest {
    private Activity a1 = new Activity("1", "path", "cooking a meal", 42, "Imagination");
    private Activity a2 = new Activity("2", "path", "heating a house for 1 hour", 1100, "Imagination");
    private Activity a3 = new Activity("3", "path", "mining bitcoin for 2 hours", 1059, "Imagination");
    private Activity a4 = new Activity("4", "path", "cooking a meal", 42, "Imagination");
    private Activity a5 = new Activity("5", "path", "heating a house for 1 hour", 1100, "Imagination");
    private Activity a6 = new Activity("6", "path", "mining bitcoin for 2 hours", 1059, "Imagination");
    private HashSet<Activity> activities = new HashSet<>(3);

    @Test
    void constructorTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        HowMuchQuestion q1 = new HowMuchQuestion(activities);
        assertNotNull(q1);
    }

    @Test
    void getQuestionTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        HowMuchQuestion q1 = new HowMuchQuestion(activities);
        assertEquals(q1.getQuestion(), "How much energy does this consume?");
    }

    @Test
    void equalsTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        HowMuchQuestion q1 = new HowMuchQuestion(activities);
        HowMuchQuestion q2 = new HowMuchQuestion(activities);
        assertEquals(q1, q2);
    }

    @Test
    void notEqualsTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        HashSet<Activity> activities2 = new HashSet<>(1);
        activities2.add(a4);
        activities2.add(a5);
        activities2.add(a6);
        HowMuchQuestion q1 = new HowMuchQuestion(activities);
        HowMuchQuestion q2 = new HowMuchQuestion(activities2);
        assertNotEquals(q1, q2);
    }

    @Test
    void hashCodeTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        HowMuchQuestion q1 = new HowMuchQuestion(activities);
        HowMuchQuestion q2 = new HowMuchQuestion(activities);
        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    void notEqualHashCodeTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        HashSet<Activity> activities2 = new HashSet<>(1);
        activities2.add(a4);
        activities2.add(a5);
        activities2.add(a6);
        HowMuchQuestion q1 = new HowMuchQuestion(activities);
        HowMuchQuestion q2 = new HowMuchQuestion(activities2);
        assertNotEquals(q1, q2);
        assertNotEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void hasToString() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        var actual = new HowMuchQuestion(activities).toString();
        assertTrue(actual.contains(HowMuchQuestion.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("answers"));
    }

}
