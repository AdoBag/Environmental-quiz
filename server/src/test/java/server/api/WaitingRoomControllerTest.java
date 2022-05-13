package server.api;

import commons.Player;
import server.WaitingRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.OK;

public class WaitingRoomControllerTest {
    private final Player player = new Player("name", 0, 5, 4, true);
    private final Player sameNameP1 = new Player("name");
    private final Player player2 = new Player("anotherName", 0, 15, 18, true);

    private List<Player> players1 = new ArrayList<>();
    private List<Player> players2 = new ArrayList<>(List.of(player, player2, sameNameP1));
    private WaitingRoomController sut;
    private WaitingRoomController sut2;
    private final WaitingRoom w1 = new WaitingRoom(players1);
    private final WaitingRoom w2 = new WaitingRoom(players2);

    @BeforeEach
    void setup() {
        sut = new WaitingRoomController(w1);
        sut2 = new WaitingRoomController(w2);
    }

    @Test
    void add() {
        var actual = sut.addPlayer(player2);
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    void getWaitingRoomPlayers() {
        var actual = sut.getWaitingRoomPlayers();
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    void getNumberOfPlayers() {
        var actual = sut.getNumberOfPlayers();
        assertEquals(OK, actual.getStatusCode());
    }

    @Test
    void removePlayer() {
        var actual = sut.removePlayerWaitingRoom(player);
        assertEquals(OK, actual.getStatusCode());
    }
}