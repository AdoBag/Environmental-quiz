/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import commons.Game;
import commons.Question;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.GameRepository;

import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameRepository repo;
    private final ActivityController activityController;

    public GameController(GameRepository repo, ActivityController activityController) {
        this.repo = repo;
        this.activityController = activityController;
    }

    /**
     * retrieve (GET) all entries from the game repository.
     * @return all entries in the repo
     */
    @GetMapping(path = { "", "/" })
    public List<Game> getAll() {
        return repo.findAll();
    }

    /**
     * Endpoint used to fetch (GET) a game of given id from the repo.
     * @param id = id of game to be fetched
     * @return badRequest if id is less than 0 or if Game with that id does not exist on repo or is Empty.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Game> getById(@PathVariable("id") long id) {
        if (id < 0 || !repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Game> response = repo.findById(id);
        if (response.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(response.get());
    }

    /**
     * Endpoint to Post (add) provided Game to the repo.
     * @param game = game to be added.
     * @return badRequest if game is null or if any of its fields are null or empty.
     */
    @PostMapping(path = { "", "/" })
    public ResponseEntity<Game> add(@RequestBody Game game) {
        if ((game.questionList == null || game.questionList.isEmpty())) {
            return ResponseEntity.badRequest().build();
        }
        Game saved = repo.save(game);
        return ResponseEntity.ok(saved);
    }

    /**
     * Endpoint used to delete Game with provided id.
     * @param id = id of Game to be deleted.
     * @return badRequest if no entry with id is present, else deleted entry
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Game> delete(@PathVariable("id") long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Game> response = repo.findById(id);
        if (response.isEmpty()) return ResponseEntity.badRequest().build();
        Game saved = response.get();
        repo.delete(saved);
        return ResponseEntity.ok(saved);
    }

    /**
     * Create a new game instance with 20 random questions and store it in the database.
     * @return id of the newly created game.
     */
    @GetMapping("/ready")
    public ResponseEntity<Long> createGame() {
        Game game = new Game(activityController.generateQuestions().getBody());
        if (game.questionList == null || game.questionList.isEmpty()) return ResponseEntity.badRequest().build();
        repo.save(game);
        return ResponseEntity.ok(game.id);
    }

    /**
     * Gives the next question of the game based on the roundcounter.
     * @param id of the game being played.
     * @return badRequest if game doesn't exist, else next question of the game.
     */
    @GetMapping("/getnextquestion/{id}")
    public ResponseEntity<Question> getNextQuestion(@PathVariable("id") long id) {
        Optional<Game> response = repo.findById(id);
        if (response.isEmpty()) return ResponseEntity.badRequest().build();
        Game game = response.get();
        return ResponseEntity.ok(game.getCurrentQuestion());
    }

    /**
     * Increases the roundcounter by 1 for the current game
     * such that getCurrentQuestion will return the next question in the set.
     * @param id of the game being played.
     */
    @GetMapping("/nextround/{id}")
    public ResponseEntity<Byte> nextRound(@PathVariable("id") long id) {
        Game game = getById(id).getBody();
        if (game != null) {
            game.nextRound();
            repo.save(game);
            return ResponseEntity.ok(game.roundCounter);
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Starts the timer for the multiplayer game and calls nextround everytime.
     * @param id - gameid for the game that is gettting started
     */
    @GetMapping("/starttimer/{id}")
    public void startGameTimer(@PathVariable("id") long id) {
        Game game = getById(id).getBody();
        final boolean[] firstTime = {true};
        if (game != null) {
            game.nextRound();
            repo.save(game);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    game.nextRound();
                    repo.save(game);
                    if (firstTime[0] && game.roundCounter == 11) {
                        game.roundCounter--;
                        firstTime[0] = false;
                    }
                    if (game.roundCounter == 19) {
                        timer.cancel();
                        timer.purge();
                        delete(id);
                    }
                }
            }, 9000, 14000);
        }
    }
}