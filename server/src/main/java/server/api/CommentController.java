package server.api;


import commons.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import server.database.CommentRepository;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private CommentRepository repo;

    public CommentController(CommentRepository repo) {
        this.repo = repo;
    }

    @GetMapping(path = {"", "/"})
    public List<Comment> getAll() {
        return repo.findAll();
    }

    /**
     * Message mapping for adding comments on the chat using the websockets.
     * @param q - comment made by player.
     * @param id - game id where request comes from and should be sent to.
     * @return the provided comment gets send back to all players.
     */
    @MessageMapping
    @SendTo("/topic/comment/{id}")
    public Comment addComment(Comment q,@RequestParam("id") long id) {
        add(q);
        return q;
    }

    /**
     * Send a message to all players when clicking on the start button and send them to the game.
     * @param q - comment containing the gameid.
     * @return - the gameid.
     */
    @MessageMapping
    @SendTo("/topic/comment")
    public Comment addGame(Comment q) {
        add(q);
        return q;
    }

    /**
     * Store the comment in the database.
     * @param comment - comment made by player.
     * @return - badrequest if comment is null or empty, else the provided comment.
     */
    @PostMapping(path = {"","/"})
    public ResponseEntity<Comment> add(@RequestBody Comment comment) {
        if (comment == null || comment.getText() == null) {
            return ResponseEntity.badRequest().build();
        }
        Comment saved = repo.save(comment);
        return ResponseEntity.ok(saved);
    }
}
