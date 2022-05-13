package server.api;

import commons.Player;
//import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.PlayerRepository;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/player")
public class PlayerController {
    private final PlayerRepository repo;

    public PlayerController(PlayerRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = {"","/"})
    public List<Player> getAll() {
        return repo.findAll();
    }

    /**
     * Endpoint used to fetch (GET) a Player of given id from the repo.
     * @param id = id of player to be fetched
     * @return badRequest if id is less than 0 or if Player with that id does not exist on repo or is Empty.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Player> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Player> response = repo.findById(id);
        if (response.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(response.get());
    }

    /**.
     * Add player to database, only if it is a new nickname or it already exists, but that user is offline
     * @param player = player to be added.
     * @return badRequest if player is null or if player nickname is taken, null or Empty.
     */
    @PostMapping(path = {"","/"})
    public ResponseEntity<Player> add(@RequestBody Player player) {
        if (player == null || player.nickname == null || player.nickname.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Player> existingPlayer = getAll().stream().filter(p -> p.nickname.equals(player.nickname)).findFirst();
        if (!existingPlayer.isEmpty()) {
            if (!existingPlayer.get().isOnline) {
                Player saved = existingPlayer.get();
                saved.isOnline = true;
                return ResponseEntity.ok(repo.save(saved));
            }
            else return ResponseEntity.badRequest().build();
        }
        //listeners.forEach((k, l) -> l.accept(player)); // notifies listeners when add is called

        return ResponseEntity.ok(repo.save(player));
    }

    /**
     * Update the highscore of a player.
     * Also notifies listeners when score is updated in order for leaderboard to function with longpolling
     * @param player who's highscore has changed.
     * @return the updated player.
     */
    @PostMapping(path = {"/updatescore","/"})
    public ResponseEntity<Player> updateScore(@RequestBody Player player) {
        if (player == null || player.nickname == null || player.nickname.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        listeners.forEach((k, l) -> l.accept(player)); // notifies listeners when this is called ie. when score is updated

        return ResponseEntity.ok(repo.save(player));
    }

    @PostMapping("/updateIsWaiting")
    public ResponseEntity<Player> updateIsWaiting(@RequestBody Player player) {
        if (player == null || player.nickname == null || player.nickname.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        listeners.forEach((k, l) -> l.accept(player)); // notifies listeners when this is called ie. when score is updated

        return ResponseEntity.ok(repo.save(player));
    }

    /**
     * Delete Player object with certain id from database.
     * @param id - the id of a player
     * @return either a badRequest if id is less than zero or if Player with that id
     *         does not exist on repo or is Empty, or return the removed object.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Player> delete(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        else {
            Optional<Player> response = repo.findById(id);
            if (response.isEmpty()) return ResponseEntity.badRequest().build();
            Player saved = response.get();
            repo.delete(saved);
            return ResponseEntity.ok(saved);
        }
    }

    /**.
     * sorts the players based on highscore
     * @return sorted playerlist
     */
    @GetMapping("/leaderboard")
    public List<Player> getLeaderboard() {
        List<Player> players = getAll();
        players = players.stream()
                .sorted((p1, p2) -> {
                    return p2.highscore - p1.highscore;
                }).collect(Collectors.toList());
        return players;
    }

    /**.
     * sorts players in a certain game based on score
     * @param gameID id for the current game
     * @return sorted playerlist
     */
    @GetMapping("/igleaderboard/{gameid}")
    public List<Player> getIGLeaderboard(@PathVariable("gameid") long gameID) {
        List<Player> players = new ArrayList<>();
        for (Player player : getAll()) {
            if (player.gameId == gameID) {
                players.add(player);
            }
        }
        players = players.stream()
                .sorted((p1, p2) -> {
                    return p2.score - p1.score;
                }).collect(Collectors.toList());
        return players;
    }

    /**.
     * return the id associated to the username given
     * @param name username of the player
     * @return id of the player
     */
    @GetMapping("getid/{name}")
    public ResponseEntity<Long> getId(@PathVariable("name") String name) {
        for (Player player : getAll()) {
            if (player.nickname.equals(name)) {
                return ResponseEntity.ok(player.id);
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /**.
     * updates the gameid of the player to the game which they are in or -1 else
     * @param id of the player
     * @param gameId of the game or -1
     * @return updated player
     */
    @GetMapping("updategameid/{id}/{gameid}")
    public ResponseEntity<Player> updateGameId(@PathVariable("id") long id, @PathVariable("gameid") long gameId) {
        Optional<Player> response = repo.findById(id);
        if (response.isEmpty()) return ResponseEntity.badRequest().build();
        Player player = response.get();
        player.gameId = gameId;
        return ResponseEntity.ok(repo.save(player));
    }

    /**.
     * set the boolean isOnline of player with certain name to false
      * @param name nickname of player that is leaving
     * @return badrequest if no such player exists, else the player that has left.
     */
    @GetMapping("/leave/{name}")
    public ResponseEntity<Player> leave(@PathVariable String name) {
        Optional<Player> possibleLeaver = getAll().stream().filter(p -> p.nickname.equals(name)).findFirst();
        if (!possibleLeaver.isEmpty()) {
            Player leaver = possibleLeaver.get();
            leaver.isOnline = false;
            return ResponseEntity.ok(repo.save(leaver));
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Sets the boolean waitingroom for the provided player and saves it in the database.
     * @param name - nickname of the player that entered the waitingroom.
     * @param p - true or false, depends on whether player leaves or joins the waitingroom.
     * @return - The player with the new waitingroom value.
     */
    @PostMapping("/waiting/{name}/{p}")
    public ResponseEntity<Player> setWaiting(@PathVariable("name") String name, @PathVariable("p") boolean p) {
        for (Player player : getAll()) {
            if (player.nickname.equals(name)) {
                player.setWaitingroom(p);
                return ResponseEntity.ok(repo.save(player));
            }
        }
        return  ResponseEntity.badRequest().build();
    }

    /**
     * HashMap of listeners for leaderboard functionality.
     */
    private Map<Object, Consumer<Player>> listeners = new HashMap<>();

    /**
     * Used to get updates to player highscores for leaderboard.
     * @return either a NO_CONTENT status response if there's no update for 5 seconds (timeout),
     *         or the player as a response entity
     */
    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<Player>> getUpdates() {

        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Player>>(5000L, noContent);

        var key = new Object();
        listeners.put(key, p -> {
            res.setResult(ResponseEntity.ok(p));
        });
        res.onCompletion(() -> {
            listeners.remove(key);
        });

        return res;
    }
}
