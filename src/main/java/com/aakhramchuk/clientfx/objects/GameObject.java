package com.aakhramchuk.clientfx.objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class GameObject {
    private ObservableList<GamePlayer> players = FXCollections.observableArrayList();

    public GameObject(List<GamePlayer> players) {
        this.players.setAll(players);
    }

    public ObservableList<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players.setAll(players);
    }
}
