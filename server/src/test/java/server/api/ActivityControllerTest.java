package server.api;

import commons.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

class ActivityControllerTest {
    public int nextInt;
    private MyRandom random;
    private TestActivityRepository repo;
    private ActivityController sut;
    private ActivityController sut2;
    private static final Activity a1 = new Activity("1", "aPath", "title", 301, "imagination");
    private static final Activity a2 = new Activity("2", "aPath", "anotherTitle", 700, "imagination");
    private static final Activity a3 = new Activity("3", "aPath", "anotherTitle", 900, "imagination");

    @BeforeEach
    void setup() {
        random = new MyRandom();
        repo = new TestActivityRepository();
        sut = new ActivityController(random, repo);
        sut2 = new ActivityController(new Random(), repo);
    }

    @Test
    void addNull() {
        var actual = sut.add(new Activity("0", null, null, 0, null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    void add() {
        var actual = sut.add(a1);
        assertEquals(OK, actual.getStatusCode());
        assertTrue(repo.calledMethods.contains("save"));
    }

    @Test
    void getAll() {
        List<Activity> activities = new ArrayList<>(2);
        sut.add(a1);
        activities.add(a1);
        sut.add(a2);
        activities.add(a2);
        assertEquals(activities, sut.getAll());
        assertTrue(repo.calledMethods.contains("findAll"));
    }

    @Test
    void getById() {
        sut.add(a1);
        assertEquals(a1, sut.getById("1").getBody());
        assertTrue(repo.calledMethods.contains("findById"));
    }

    @Test
    void delete() {
        sut.add(a1);
        assertEquals(a1, sut.delete("1").getBody());
        assertEquals(BAD_REQUEST, sut.getById("1").getStatusCode());
        assertTrue(repo.calledMethods.contains("delete"));
    }

    @Test
    void getRandom() {
        sut.add(a1);
        sut.add(a2);
        nextInt = 1;
        var actual = sut.getRandom();
        assertTrue(random.wasCalled);
        assertEquals(a2, actual.getBody());
    }

    @Test
    void generateComparisonQuestion() {
        sut2.add(a1);
        sut2.add(a2);
        sut2.add(a3);
        var actual = sut2.generateComparisonQuestion();
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @SuppressWarnings("serial")
    public class MyRandom extends Random {

        public boolean wasCalled = false;

        @Override
        public int nextInt(int bound) {
            wasCalled = true;
            return nextInt;
        }
    }
}