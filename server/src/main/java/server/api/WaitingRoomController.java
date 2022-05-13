package server.api;

import commons.MultiPlayer;
import commons.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.WaitingRoom;

import java.util.*;
import java.util.function.Consumer;


@RestController
@RequestMapping("/api/waiting_room")
public class WaitingRoomController {

    public WaitingRoom waitingRoom;

    /**
     * Constructor for waiting room.
     * @param waitingRoom waiting room
     */
    public WaitingRoomController(WaitingRoom waitingRoom) {
        this.waitingRoom = waitingRoom;
    }


    private Map<Object, Consumer<MultiPlayer>> listeners = new HashMap<>();

    /**
     * Gets an update if anything has changed.
     * @return res
     */
    @GetMapping("/update")
    public DeferredResult<ResponseEntity<MultiPlayer>> getUpdate() {
        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<MultiPlayer>>(5000L,noContent);
        var key = new Object();
        listeners.put(key,c -> {
            res.setResult(ResponseEntity.ok(c));
        });
        res.onCompletion(() -> {
            listeners.remove(key);
        });
        return res;
    }

    /**
     * Endpoint used to get all players in the waiting room.
     * @return all players in the waiting room
     */
    @GetMapping(path = {"/all_players"})
    public ResponseEntity<List<Player>> getWaitingRoomPlayers() {
        return ResponseEntity.ok(waitingRoom.getPlayers());
    }


    /**
     * Endpoint used to get the number of players in the waiting room.
     * @return the number of players in the waiting room
     */
    @GetMapping(path = {"/players_number"})
    public ResponseEntity<Integer> getNumberOfPlayers() {
        return ResponseEntity.ok(waitingRoom.getNumberOfPlayers());
    }

    /**
     * Adds a player to the waiting room.
     * @return the player is added
     */
    @PostMapping(path = {"/player"})
    public ResponseEntity<Player> addPlayer(@RequestBody Player player) {
        waitingRoom.addPlayerToWaitingRoom(player);
        return ResponseEntity.ok(player);
    }

    /**
     * Removes a player from the waiting room.
     * @return ok if the player has been added
     */
    @DeleteMapping(path = {"/remove_player"})
    public ResponseEntity<Player> removePlayerWaitingRoom(@RequestBody Player player) {
        waitingRoom.removePlayerFromWaitingRoom(player);
        return ResponseEntity.ok(player);
    }
}

