package commons;

import java.util.List;

/**
 * Multiplayer class to store the game ID and list of players in the game.
 */
public class MultiPlayer extends commons.Game {

    private List<Player> players;

    public MultiPlayer() {
        super();
    }

    /**
     * Constructor of MultiPlayer.
     * @param players - list of players
     */
    public MultiPlayer(List<Player> players) {
        super();
        this.players = players;
    }

    /**
     * Removes a player from the game.
     * @param player - the player to be removed
     * @return - returns true if the player has been removed
     */
    public boolean removePlayer(Player player) {
        return this.players.remove(player);
    }
}
