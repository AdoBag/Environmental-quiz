package server.api;

import commons.Activity;
import commons.ComparisonQuestion;
import commons.Game;
import commons.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

class GameControllerTest {
    private TestGameRepository repo;
    private GameController sut;
    private ActivityController activityController;
    private final HashSet<Question> questionList = new HashSet<>(2);
    private ComparisonQuestion q;
    private ComparisonQuestion q2;
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
        repo = new TestGameRepository();
        activityController = new ActivityController(new Random(), new TestActivityRepository());
        sut = new GameController(repo, activityController);
    }

    @Test
    void addNull() {
        var actual = sut.add(new Game(null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    void add() {
        var actual = sut.add(g1);
        assertEquals(OK, actual.getStatusCode());
        assertTrue(repo.calledMethods.contains("save"));
    }

    @Test
    void getAll() {
        List<Game> games = new ArrayList<>(2);
        sut.add(g1);
        games.add(g1);
        sut.add(g2);
        games.add(g2);
        assertEquals(games, sut.getAll());
        assertTrue(repo.calledMethods.contains("findAll"));
    }

    @Test
    void getById() {
        sut.add(g1);
        assertEquals(g1, sut.getById(0).getBody());
        assertTrue(repo.calledMethods.contains("findById"));
    }

    @Test
    void delete() {
        sut.add(g1);
        assertEquals(g1, sut.delete((long) 0).getBody());
        assertEquals(BAD_REQUEST, sut.getById(0).getStatusCode());
        assertTrue(repo.calledMethods.contains("delete"));
    }

    @Test
    void createGame() {
        assertEquals(BAD_REQUEST, sut.createGame().getStatusCode());
    }

    @Test
    void getNextQuestion() {
        sut.add(g1);
        System.out.println(sut.getById(0));
        assertEquals(q2, sut.getNextQuestion(0).getBody());
        sut.nextRound(0);
        assertEquals(q, sut.getNextQuestion(0).getBody());
    }

    @Test
    void nextRound() {
        sut.add(g1);
        assertEquals((byte) 1, sut.nextRound(0).getBody());
        assertEquals((byte) 2, sut.nextRound(0).getBody());
    }



}