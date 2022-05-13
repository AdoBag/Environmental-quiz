package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private final HashSet<Question> questionList = new HashSet<>(2);
    private Question q;
    private Question q2;
    private Game g1;
    private Game g11;
    private Game g2;

    @BeforeEach
    void setup() {
        HashSet<Activity> answers = new HashSet<>(2);
        HashSet<Activity> answers2 = new HashSet<>(2);
        Activity a1 = new Activity("3", "aPath", "title", 301, "imagination");
        Activity a2 = new Activity("4", "aPath", "name", 204, "imagination");
        Activity a3 = new Activity("5", "aPath", "something", 405, "imagination");
        Activity a4 = new Activity("9", "aPath", "cool", 58, "imagination");
        answers.add(a1);
        answers.add(a2);
        answers.add(a3);
        answers2.add(a3);
        answers2.add(a2);
        answers2.add(a4);
        this.q = new ComparisonQuestion(answers);
        this.q2 = new ComparisonQuestion(answers2);
        this.questionList.add(q);
        this.questionList.add(q2);
        this.g1 = new Game(questionList);
        this.g11 = new Game(questionList);
        this.g2 = new Game(questionList);
        g1.nextRound();
        g11.nextRound();
    }

    @Test
    void constructorTest() {
        Game game = new Game(this.questionList);
        assertNotNull(game);
        assertEquals(this.questionList, game.questionList);
        game.nextRound();
        assertEquals((byte) 0, game.roundCounter);
    }

    @Test
    void testEquals() {
        assertEquals(g1, g11);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(g1,g2);
    }

    @Test
    void testHashCode() {
        assertEquals(g1.hashCode(), g11.hashCode());
    }

    @Test
    void testNotHashCode() {
        assertNotEquals(g1.hashCode(), g2.hashCode());
    }

    @Test
    void testToString() {
        var actual = new Game(this.questionList).toString();
        assertTrue(actual.contains(Game.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("questionList"));
    }

    @Test
    void nextRound() {
        assertEquals((byte) 0, g1.roundCounter);
        g1.nextRound();
        assertEquals((byte) 1, g1.roundCounter);
    }

    @Test
    void getCurrentQuestion() {
        assertEquals(q2, g1.getCurrentQuestion());
        g1.nextRound();
        assertEquals(q, g1.getCurrentQuestion());
    }
}