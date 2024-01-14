package com.aakhramchuk.clientfx.objects;

import com.aakhramchuk.clientfx.containers.MainContainer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GameObject {
    private ObservableList<GamePlayer> players = FXCollections.observableArrayList();
    private ObservableList<GamePlayer> winners = FXCollections.observableArrayList();

    /**
     * Initializes a new GameObject with the provided list of players.
     *
     * @param players A list of GamePlayer objects representing the players in the game.
     */
    public GameObject(List<GamePlayer> players) {
        this.players.setAll(players);
    }

    /**
     * Retrieves the observable list of players in the game.
     *
     * @return An ObservableList containing the GamePlayer objects representing the players.
     */
    public ObservableList<GamePlayer> getPlayers() {
        return players;
    }

    /**
     * Updates the list of players in the game with a new list of players, optionally updating the application user.
     *
     * @param players             A list of GamePlayer objects representing the updated player information.
     * @param updateApplicationUser A boolean indicating whether to update the application user's information.
     */
    public void updatePlayers(List<GamePlayer> players, boolean updateApplicationUser) {
        for (GamePlayer player : this.players) {
            for (GamePlayer playerNew : players) {
                if (player.getUsername().equals(playerNew.getUsername())) {
                    if (!updateApplicationUser && player.getUsername().equals(MainContainer.getUser().getUsername())) {
                        continue;
                    }
                    player.updatePlayer(playerNew);
                }
            }
        }
    }

    /**
     * Sets the list of players in the game.
     *
     * @param players A list of GamePlayer objects representing the players to be set.
     */
    public void setPlayers(List<GamePlayer> players) {
        this.players.setAll(players);
    }

    /**
     * Retrieves the observable list of winners in the game.
     *
     * @return An ObservableList containing the GamePlayer objects representing the winners.
     */
    public ObservableList<GamePlayer> getWinners() {
        return winners;
    }


    /**
     * Sets the list of winners in the game.
     *
     * @param winners An ObservableList containing the GamePlayer objects representing the winners to be set.
     */
    public void setWinners(ObservableList<GamePlayer> winners) {
        this.winners = winners;
    }
}
