package server.api;

import java.util.*;
import java.util.function.Consumer;

import commons.Activity;
import commons.ComparisonQuestion;
import commons.GuessQuestion;
import commons.Question;
import org.springframework.http.HttpStatus;
import commons.HowMuchQuestion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import server.database.ActivityRepository;


@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final Random random;
    private final ActivityRepository repo;

    private Map<Object, Consumer<Activity>> listeners = new HashMap<>();


    public ActivityController(Random random, ActivityRepository repo) {
        this.random = random;
        this.repo = repo;
    }

    /**
     * Fetch all activities from the database.
     * @return list of activities.
     */
    @GetMapping(path = {"", "/"})
    public List<Activity> getAll() {
        return repo.findAll();
    }

    /**
     * Find size of repository.
     * @return - repository size.
     */
    @GetMapping("/count")
    public long getCount() {
        return repo.count();
    }

    /**
     * Endpoint used to fetch (GET) an activity of given id from the repo.
     * @param id - id of activity to be fetched
     * @return - returns badRequest if id is less than 0 or if Activity with that id does not exist on repo or is Empty.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Activity> getById(@PathVariable("id") String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Activity> response = repo.findById(id);
        if (response.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(response.get());
    }

    /**.
     * fetch a random entry from the repository
     * @return - returns badRequest if repo is empty or random entry
     */
    @GetMapping("rnd")
    public ResponseEntity<Activity> getRandom() {
        if (repo.count() == 0) return ResponseEntity.badRequest().build();
        var entries = getAll();
        var idx =  random.nextInt(entries.size());
        return ResponseEntity.ok(entries.get(idx));
    }

    /**
     * Endpoint to Post (add) provided Activity to the repo.
     * @param activity = activity to be added.
     * @return - returns badRequest if Activity is null or if any of its fields are null or empty.
     */
    @PostMapping(path = {"","/"})
    public ResponseEntity<Activity> add(@RequestBody Activity activity) {
        if (activity.id == null || activity == null || activity.title == null
                || activity.title.isEmpty() || activity.consumption_in_wh == null || activity.image_path == null
                || activity.image_path.isEmpty() || activity.source == null || activity.source.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        listeners.forEach((k, l) -> l.accept(activity));
        Activity saved = repo.save(activity);
        return ResponseEntity.ok(saved);
    }

    /**
     * Endpoint used to delete Activity with provided id.
     * @param id = id of Activity to be deleted.
     * @return - returns badRequest if id is less than 0 or if Activity with that id does not exist on repo or is Empty.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Activity> delete(@PathVariable("id") String id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Activity> response = repo.findById(id);
        if (response.isEmpty()) return ResponseEntity.badRequest().build();
        Activity saved = response.get();
        repo.delete(saved);
        return ResponseEntity.ok(saved);
    }

    /**
     * Fetch random activities from the database (for testing) and generate a list of 20 comparison questions.
     * @return - returns responseEntity as list of 20 ComparisonQuestions or badRequest.
     */
    @GetMapping("/compq")
    public ResponseEntity<HashSet<Question>> generateComparisonQuestion() {
        if (repo.count() < 60) return ResponseEntity.badRequest().build();
        var activities = getAll();
        HashSet<Activity> totalActivitySet = new HashSet<>(60);
        HashSet<Question> questions = new HashSet<>(20);
        int tries = 0;

        while (totalActivitySet.size() != 60) {
            var idx = random.nextInt(activities.size());
            Activity random = activities.get(idx);
            if (random != null) {
                totalActivitySet.add(activities.get(idx));
            } else tries++;
            if (tries == 50) return ResponseEntity.badRequest().build();
        }

        Iterator<Activity> activityIterator = totalActivitySet.iterator();
        while (questions.size() != 20) {
            HashSet<Activity> activitySet = new HashSet<>(3);
            while (activitySet.size() != 3) {
                activitySet.add(activityIterator.next());
            }
            Question cq = new ComparisonQuestion(activitySet);
            questions.add(cq);
        }
        return ResponseEntity.ok(questions);
    }

    /**
     * Fetch random activities from the database and generate a list of 20 questions.
     * @return - returns responseEntity as list of 20 GuessQuestions or badRequest.
     */
    @GetMapping("/getQuestions")
    public ResponseEntity<HashSet<Question>> generateQuestions() {
        if (repo.count() < 60) return ResponseEntity.badRequest().build();
        var activities = getAll();
        HashSet<Activity> totalActivitySet = new HashSet<>(60);
        HashSet<Question> questions = new HashSet<>(20);
        int tries = 0;
        while (totalActivitySet.size() != 60) {
            var idx = random.nextInt(activities.size());
            Activity random = activities.get(idx);
            if (random != null) {
                totalActivitySet.add(activities.get(idx));
            } else tries++;
            if (tries == 50) return ResponseEntity.badRequest().build();
        }
        Iterator<Activity> activityIterator = totalActivitySet.iterator();
        HashSet<Activity> activitySet = new HashSet<>(3);
        while (questions.size() != 20) {
            activitySet.clear();
            while (activitySet.size() != 3) {                   // Adds a comparison question
                activitySet.add(activityIterator.next());
            }
            questions.add(new ComparisonQuestion(activitySet));
            activitySet.clear();
            while (activitySet.size() != 3) {                   // Adds a howmuch question
                activitySet.add(activityIterator.next());
            }
            questions.add(new HowMuchQuestion(activitySet));
            activitySet.clear();
            if (questions.size() < 20) {
                while (activitySet.size() != 1) {               // Adds a guess question
                    activitySet.add(activityIterator.next());
                }
                questions.add(new GuessQuestion(activitySet));
            }
        }
        return ResponseEntity.ok(questions);
    }

    /**
     * Gets the updates on activities for the admin screen.
     * @return - returns either a NO-CONTENT response after a 5 seconds timeout or the added activity otherwise.
     */
    @GetMapping("/updates")
    public DeferredResult<ResponseEntity<Activity>> getUpdates() {

        var noContent = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        var res = new DeferredResult<ResponseEntity<Activity>>(5000L, noContent);

        var key = new Object();
        listeners.put(key, a -> {
            res.setResult(ResponseEntity.ok(a));
        });
        res.onCompletion(() -> {
            listeners.remove(key);
        });

        return res;
    }
}
