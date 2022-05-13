package server.api;

import commons.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

class PlayerControllerTest {
    private TestPlayerRepository repo;
    private PlayerController sut;
    private static final Player player = new Player("name", 0, 5, 4, true);
    private static final Player sameNameP1 = new Player("name");
    private static final Player player2 = new Player("anotherName", 0, 15, 18, true);

    @BeforeEach
    void setup() {
        repo = new TestPlayerRepository();
        sut = new PlayerController(repo);
    }

    @Test
    void addNull() {
        var actual = sut.add(new Player(null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    void add() {
        var actual = sut.add(player);
        assertEquals(OK, actual.getStatusCode());
        assertTrue(repo.calledMethods.contains("save"));
    }

    @Test
    void getAll() {
        List<Player> players = new ArrayList<>(2);
        sut.add(player);
        players.add(player);
        sut.add(player2);
        players.add(player2);
        assertEquals(players, sut.getAll());
        assertTrue(repo.calledMethods.contains("findAll"));
    }

    @Test
    void getById() {
        sut.add(player);
        assertEquals(player, sut.getById(0).getBody());
        assertTrue(repo.calledMethods.contains("findById"));
    }

    @Test
    void delete() {
        sut.add(player);
        assertEquals(player, sut.delete(0).getBody());
        assertEquals(BAD_REQUEST, sut.getById(0).getStatusCode());
        assertTrue(repo.calledMethods.contains("delete"));
    }

    @Test
    void getLeaderboard() {
        sut.add(player);
        sut.add(player2);
        List<Player> sortedList = new ArrayList<>(2);
        sortedList.add(player2);
        sortedList.add(player);
        assertEquals(sortedList, sut.getLeaderboard());
    }

    @Test
    void updateGameId() {
        sut.add(player);
        player.gameId = 8;
        assertEquals(8, sut.updateGameId(0, 8).getBody().gameId);
    }

    @Test
    void getId() {
        sut.add(player);
        assertEquals(0, sut.getId("name").getBody());
    }

    @Test
    void leave() {
        sut.add(player);
        sut.leave(player.nickname);
        assertFalse(player.isOnline);
    }

    @Test
    void addOfflinePlayer() {
        sut.add(player);
        assertEquals(BAD_REQUEST, sut.add(sameNameP1).getStatusCode());
        sut.leave(player.nickname);
        assertEquals(player, sut.add(sameNameP1).getBody());
    }
}