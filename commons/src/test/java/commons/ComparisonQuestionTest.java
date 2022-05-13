package commons;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonQuestionTest {
    private Activity a1 = new Activity("1", "path", "cooking a meal", 42, "Imagination");
    private Activity a2 = new Activity("2", "path", "heating a house for 1 hour", 1100, "Imagination");
    private Activity a3 = new Activity("3", "path", "mining bitcoin for 2 hours", 1059, "Imagination");
    private HashSet<Activity> activities = new HashSet<>(3);


    @Test
    void constructorTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        ComparisonQuestion q1 = new ComparisonQuestion(activities);
        assertNotNull(q1);
    }

    @Test
    void equalsTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        ComparisonQuestion q1 = new ComparisonQuestion(activities);
        ComparisonQuestion q2 = new ComparisonQuestion(activities);
        assertEquals(q1, q2);
    }

    @Test
    void notEqualsTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        HashSet<Activity> activities2 = new HashSet<>(1);
        activities2.add(a1);
        ComparisonQuestion q1 = new ComparisonQuestion(activities);
        ComparisonQuestion q2 = new ComparisonQuestion(activities2);
        assertNotEquals(q1, q2);
    }

    @Test
    void hashCodeTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        ComparisonQuestion q1 = new ComparisonQuestion(activities);
        ComparisonQuestion q2 = new ComparisonQuestion(activities);
        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    void notEqualHashCodeTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        HashSet<Activity> activities2 = new HashSet<>(1);
        activities2.add(a1);
        ComparisonQuestion q1 = new ComparisonQuestion(activities);
        ComparisonQuestion q2 = new ComparisonQuestion(activities2);
        assertNotEquals(q1, q2);
        assertNotEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    public void hasToString() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        var actual = new ComparisonQuestion(activities).toString();
        assertTrue(actual.contains(ComparisonQuestion.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("answers"));
    }

    @Test
    void getQuestionTest() {
        ComparisonQuestion q = new ComparisonQuestion();
        assertEquals("What requires more energy?", q.getQuestion());
    }

    @Test
    void getCorrectAnswerTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        ComparisonQuestion q = new ComparisonQuestion(activities);
        assertEquals(a2, q.getCorrectAnswer(q.answers));
    }

    @Test
    void getWrongAnswersTest() {
        activities.add(a1);
        activities.add(a2);
        activities.add(a3);
        HashSet<Activity> wrongAnswers = new HashSet<>(2);
        wrongAnswers.add(a1);
        wrongAnswers.add(a3);
        ComparisonQuestion q = new ComparisonQuestion(activities);
        assertEquals(wrongAnswers, q.getWrongAnswers(q.getCorrectAnswer(q.answers), q.answers));
    }
}