package com.aakhramchuk.clientfx.objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GameObject {
    private ObservableList<GamePlayer> players = FXCollections.observableArrayList();
    private ObservableList<GamePlayer> winners = FXCollections.observableArrayList();

    public GameObject(List<GamePlayer> players) {
        this.players.setAll(players);
    }

    public ObservableList<GamePlayer> getPlayers() {
        return players;
    }

    public void updatePlayers(List<GamePlayer> players) {
        for (GamePlayer player : this.players) {
            for (GamePlayer playerNew : players) {
                if (player.getUsername().equals(playerNew.getUsername())) {
                    player.updatePlayer(playerNew);
                }
            }
        }
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players.setAll(players);
    }

    public ObservableList<GamePlayer> getWinners() {
        return winners;
    }

    public void setWinners(ObservableList<GamePlayer> winners) {
        this.winners = winners;
    }
}
