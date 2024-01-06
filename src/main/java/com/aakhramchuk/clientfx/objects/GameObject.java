package com.aakhramchuk.clientfx.objects;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    List<GamePlayer> players = new ArrayList<GamePlayer>();

    public GameObject(List<GamePlayer> players) {
        this.players = players;
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players = players;
    }

    @Override
    public String toString() {
        StringBuilder plyersString = new StringBuilder();

        for (GamePlayer player : players) {
            plyersString.append(player.toString()).append("\n");
        }

        return "===============================================================================================\n" +
        "                                        GAME IS ACTIVE\n" +
        "===============================================================================================\n\n"+
                plyersString + "===============================================================================================\n\n";
    }
}

