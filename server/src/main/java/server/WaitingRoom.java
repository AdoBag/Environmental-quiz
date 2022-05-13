package server;

import commons.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores all players in the waiting room.
 */
public class WaitingRoom {
    private List<Player> players;


    public WaitingRoom() {
        players = new ArrayList<>();
    }

    /**
     * Constructor for the waiting room object.
     * @param players list of players
     */
    public WaitingRoom(List<Player> players) {
        this.players = players;
    }

    /**
     * Adds the player to the waiting room.
     * @param player player to be added
     * @return true if the player has been added
     */
    public boolean addPlayerToWaitingRoom(Player player) {
        if (player == null || this.players.contains(player)) return false;
        return this.players.add(player);
    }

    /**
     * Removes the player from the waiting room.
     * @param player to be removed
     * @return true if the player has been removed
     */
    public boolean removePlayerFromWaitingRoom(Player player) {
        return this.players.remove(player);
    }

    /**
     * Returns the size of the players list.
     * @return number of players in the waiting room
     */
    public int getNumberOfPlayers() {
        return this.players.size();
    }

    /**
     * Returns the list of players.
     * @return all players in the waiting room
     */
    public List<Player> getPlayers() {
        return players;
    }
}
