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
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import commons.*;

import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class ServerUtils {

    private static final String SERVER = "http://localhost:8080/";
    private static final ExecutorService EXEC = Executors.newSingleThreadExecutor();

    private static final ExecutorService adminEXEC = Executors.newSingleThreadExecutor();

    private final StompSession session = connect("ws://localhost:8080/websocket");

    /**
     * Gets all players in the waiting room.
     * @return list of players
     */
    public List<Player> getPlayersWaitingRoom() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/waiting_room/all_players")
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<>() {
                });
    }

    /**
     * Registers for updates in the waiting room.
     * @param consumer - a multiplayer entity
     */
    public void registerUpdates(Consumer<MultiPlayer> consumer) {
        EXEC.submit(() -> {
            while (!Thread.interrupted()) {
                var res = ClientBuilder.newClient(new ClientConfig())
                        .target(SERVER).path("api/waiting_room/update")
                        .request(APPLICATION_JSON) //
                        .accept(APPLICATION_JSON) //
                        .get(Response.class);
                if (res.getStatus() == 204) {
                    continue;
                }
                var c = res.readEntity(MultiPlayer.class);
                consumer.accept(c);
            }
        });
    }

    /**
     * Removes a player from the waiting room.
     * @param player - the player to be removed
     */
    public void removePlayerWaitingRoom(Player player) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/waiting_room/remove_player/" + player.id)
                .request()
                .delete();
    }

    /**
     * Adds a player to the waiting room.
     * @param player - the player to be added
     * @return - returns the added player
     */
    public Player addPlayerWaitingRoom(Player player) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/waiting_room/player")
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(player, APPLICATION_JSON), Player.class);
    }

    /**
     * Returns the number of people in the waiting room.
     * @return - returns the number of people in the waiting room
     */
    public int getNumberOfPlayersWaitingRoom() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/waiting_room/players_number")
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Integer>() {
                });
    }

    /**
     * Used to add provided player to the database,
     * used on OK button press on Login Screen.
     * @param player - the player to be added
     * @return - returns the added player
     */
    public Player addPlayer(Player player) {
        return ClientBuilder.newClient(new ClientConfig())//
                .target(SERVER).path("api/player")//
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(player, APPLICATION_JSON), Player.class);
    }

    /**
     * Used to add provided activity to the database.
     * Used in Admin Screen, providing functionality to Import/Add Activity buttons.
     * @param activity - the activity to be added
     * @return - returns the added activity
     */
    public Activity addActivity(Activity activity) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/activities")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(activity, APPLICATION_JSON), Activity.class);
    }

    /**
     * Used to delete provided activity from the database.
     * Used in Admin Screen, providing functionality to Delete Activity buttons
     * @param activity - the activity to be deleted
     */
    public void deleteActivity(Activity activity) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/activities/" + activity.getId())
                .request()
                .delete();
    }

    /**
     * Deletes a player by id.
     * @param player - the player to be deleted
     */
    public void deletePlayer(Player player) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/player/" + player.getId())
                .request()
                .delete();
    }

    /**
     * Gets a player based on their id.
     * @param id player id
     * @return - returns a player with this id
     */
    public Player getPlayerByID(long id) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/player/" + id) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Player>() {});
    }

    /**
     * Gets all players in the database in a List.
     * @return - returns a list of players in the database
     */
    public List<Player> getPlayers() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/player") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Player>>() {});
    }

    /**
     * Gets all activities in the database in a List.
     * @return - returns all activities in the database
     */
    public List<Activity> getActivities() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/activities") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<List<Activity>>() {});
    }

    /**
     * Gets the amount of activities.
     * @return - returns the amount of activities
     */
    public long getCount() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/activities/count")
                .request()
                .get(Long.class);
    }

    /**
     * This function creates the server construction for the websocket registering.
     * @param url - this is the url for websocket connection
     * @return Connect the server to use stomp ???
     */
    private StompSession connect(String url) {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(url, new StompSessionHandlerAdapter() { }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } throw new IllegalStateException();
    }

    /**
     * This function is used for registering for messages. This allows the client to register for the
     * endpoint and then receive the messages that are registered to dest.
     * @param dest string for the destination endpoint
     * @param consumer this consumes the payload that we send to the server
     */
    public void registerforMessages(String dest,Consumer<Comment> consumer) {
        session.subscribe(dest,new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Comment.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((Comment) payload);
            }
        });
    }

    /**
     * This function is used to send the message itself.
     * @param dest - string for the destination endpoint
     * @param o - message to be sent
     */
    public void send(String dest,Object o) {
        session.send(dest, o);
    }

    /**
     * Get all players in a sorted list based on highscore.
     * @return - returns sorted list containing all players
     */
    public List<Player> getLeaderboard() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/player/leaderboard")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Player>>() {});

    }

    /**
     * Get one ComparisonQuestion object which contains one correct answer Activity.
     * and a HashSet of two wrong answer Activities
     * @return - returns ComparisonQuestion type question
     */
    public ComparisonQuestion getComparisonQuestion() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/activities/compq") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<ComparisonQuestion>() {});
    }

    /**
     * Create a new game instance and receive the associated id.
     * @return - returns id of the game
     */
    public long startGame() {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/game/ready") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Long>() {});
    }

    /**
     * Increase the roundcounter on the serverside.
     * @param gameId - the id of the associated game
     * @return - returns the new current round
     */
    public byte nextRound(Long gameId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/game/nextround/" + gameId) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Byte>() {});
    }

    /**
     * Get the question for the next round.
     * @param gameId - the id of the associated game
     * @return - returns the next question
     */
    public Question getNextQuestion(Long gameId) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/game/getnextquestion/" + gameId) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Question>() {});
    }

    /**
     * Get all players in a sorted list based on score.
     * @param gameID id of a game
     * @return - returns sorted list containing all players
     */
    public List<Player> getIGLeaderboard(long gameID) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/player/igleaderboard/" + gameID)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Player>>() {});
    }


    /**
     * Return the id associated to this player.
     * @param name - nickname of the current player
     * @return - returns id of the current player
     */
    public long getId(String name) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/player/getid/" + name)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Long>() {});
    }

    /**
     * Update gameId of player when a new game starts or ends.
     * @param id - id of the current user
     * @param gameId - id of the game that will be played or -1 at the end
     * @return - returns the updated player
     */
    public Player updateGameId(long id, long gameId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/player/updategameid/" + id + "/" + gameId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Player>() {});
    }

    /**
     * Sets a player's waiting room boolean.
     * @param player player who should be updated in the server
     * @return the player
     */
    public Player updateIsWaitingRoom(Player player) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/player/updateIsWaiting") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(player, APPLICATION_JSON), Player.class);
    }

    /**
     * Sets the isOnline boolean of this player to false.
     * @param name - nickname of leaving player
     * @return - returns the player who has left
     */
    public Player leave(String name) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/player/leave/" + name) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get(new GenericType<Player>() {});
    }

    /**
     * Update the highscore of the player on the server.
     * @param player - the person who's highscore needs to be updated
     * @return the player
     */
    public Player updateScore(Player player) {
        return ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/player/updatescore") //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .post(Entity.entity(player, APPLICATION_JSON), Player.class);
    }

    /**
     * Remove the game instance associated with the gameid from the databaase.
     * @param gameId - id of the game to be removed
     */
    public void removeGame(long gameId) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/game/" + gameId)
                .request()
                .delete();
    }

    /**
     * While program is running, it constantly checks for updates (polls) and gets a response.
     * if status = 204 (no content) there is no update (timeout), so we don't process anything, just poll again
     * @param consumer - functional interface used to consume data (Player)
     */
    public void checkForUpdates(Consumer<Player> consumer) {

        EXEC.submit(() -> {
            while (!Thread.interrupted()) {
                var res = ClientBuilder.newClient(new ClientConfig()) //
                        .target(SERVER).path("api/player/updateleaderboard") //
                        .request(APPLICATION_JSON) //
                        .accept(APPLICATION_JSON) //
                        .get(Response.class);

                if (res.getStatus() == 204) {
                    continue;
                }
                var p = res.readEntity(Player.class);
                consumer.accept(p);
            }
        });
    }

    /**
     * Stops the Executor so that the thread doesn't stay running forever.
     */
    public void stop() {
        EXEC.shutdownNow();
    }

    public void startGameTimer(long gameId) {
        ClientBuilder.newClient(new ClientConfig()) //
                .target(SERVER).path("api/game/starttimer/" + gameId) //
                .request(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
                .get();
    }

    /**
     * Checks for updates in "api/activities/adminUpdate" within a thread.
     * @param consumer - the single input argument of a possible instance of an Activity
     */
    public void checkForActivityUpdates(Consumer<Activity> consumer) {

        adminEXEC.submit(() -> {
            while (!Thread.interrupted()) {
                var res = ClientBuilder.newClient(new ClientConfig()) //
                        .target(SERVER).path("api/activities/adminUpdate") //
                        .request(APPLICATION_JSON) //
                        .accept(APPLICATION_JSON) //
                        .get(Response.class);

                if (res.getStatus() == 204) {
                    continue;
                }
                var a = res.readEntity(Activity.class);
                consumer.accept(a);
            }
        });
    }

    /**
     * Stops the executor service of the admin.
     */
    public void adminLPStop() {
        adminEXEC.shutdownNow();
    }
}

