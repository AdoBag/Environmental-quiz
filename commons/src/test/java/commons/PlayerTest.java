package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private static final Player p1 = new Player("p1");
    private static final Player p11 = new Player("p1");
    private static final Player p2 = new Player("p2");
    private static final Player player = new Player("name", 150, 333, 123L, true);

    @Test
    void testConstructor() {
        Player player = new Player("nickname");
        assertNotNull(player);
        assertEquals("nickname", player.nickname);
        assertEquals(0, player.highscore);
        assertEquals(0, player.highscore);
        assertTrue(player.isOnline);
    }

    @Test
    void testEquals() {
        assertEquals(p1, p11);
    }

    @Test
    void testNotEquals() {
        assertNotEquals(p1, p2);
    }

    @Test
    void testHashCode() {
        assertEquals(p1.hashCode(), p11.hashCode());
    }

    @Test
    void testNotHashCode() {
        assertNotEquals(p1.hashCode(), p2.hashCode());
    }


    @Test
    void hasToString() {
        var actual = new Player("name").toString();
        assertTrue(actual.contains(Player.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("nickname"));
    }


    @Test
    void testCalculateScore() {
        p1.setScore(0);
        p1.calculateScore(true, 1d, 1d, 1);
        assertEquals(0, p1.getScore());
    }

    @Test
    void testCalculateScore1() {
        p11.calculateScore(false, 1d, 1d, 1);
        assertEquals(0, p11.getScore());
        p11.setScore(0);
    }

    @Test
    void testCalculateScore2() {
        p1.setScore(0);
        p1.calculateScore(true, 0d, 10d, 1);
        p1.calculateScore(false, 10000d, 10d, 1);
        p1.calculateScore(false, 20000d,  10d, 1);
        p1.calculateScore(true, 5d, 10d, 1);
        assertEquals(150, p1.getScore());
    }

    @Test
    void testCalculateScore3() {
        p1.setScore(0);
        p1.calculateScore(true, 10d, null, 1);
        p1.calculateScore(false, 10000d, 10d, 1);
        p2.calculateScore(false, 0d, 10d, 1);
        p1.calculateScore(true, null,  10d, 1);
        p2.calculateScore(true, null, 10d, 1);
        p2.calculateScore(true, 2.5d,  10d, 1);
        p1.calculateScore(true, 2.5d, 10d, 1);
        assertEquals(75, p2.getScore());
        p1.setScore(0);
        p2.setScore(0);
    }

    @Test
    void testCalculateGuessScore1() {
        p1.setScore(0);
        p1.calculateGuessScore(100L, 50L,5d,null,2);
        assertEquals(0, p1.getScore());
        p1.setScore(0);
    }

    @Test
    void testCalculateGuessScore2() {
        p1.setScore(0);
        p1.calculateGuessScore(100L, 50L,null,10d,2);
        assertEquals(0, p1.getScore());
        p1.setScore(0);
    }

    @Test
    void testCalculateGuessScore3() {
        p1.setScore(0);
        p1.calculateGuessScore(1000L, 1001L,5d,10d,1);
        assertEquals(50, p1.getScore());
        p1.setScore(0);
    }

    @Test
    void testCalculateGuessScore4() {
        p1.setScore(0);
        p1.calculateGuessScore(1000L, -1050L,5d,10d,1);
        assertEquals(0, p1.getScore());
        p1.setScore(0);
    }

    @Test
    void getNickname() {
        assertEquals("p1", p1.getNickname());
    }

    @Test
    void constructorTest() {
        assertNotNull(player);
    }

    @Test
    void getHighscore() {
        assertEquals(333, player.getHighscore());
    }

    @Test
    void getScore() {
        assertEquals(150, player.getScore());
    }

    @Test
    void setHighscore() {
        p1.setHighscore(3);
        assertEquals(3, p1.getHighscore());
        p1.setScore(0);
        p1.setHighscore(0);
    }

    @Test
    void updateScore() {
        p1.setHighscore(20);
        p1.setScore(50);
        p1.updateScore();
        assertEquals(50, p1.getHighscore());
        p1.setHighscore(0);
        p1.setScore(0);
    }

    @Test
    void setWaiting() {
        player.setWaitingroom(false);
        assertEquals(false, player.isWaitingroom);
    }
}