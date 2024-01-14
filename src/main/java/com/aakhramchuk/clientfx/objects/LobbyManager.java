package com.aakhramchuk.clientfx.objects;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyManager {

    private static final ConcurrentHashMap<Integer, Lobby> lobbiesMap = new ConcurrentHashMap<>();
    private static final ObservableList<Lobby> lobbiesList = FXCollections.observableArrayList();
    private static final Object currentLobbyLock = new Object();
    private static Lobby currentLobby;


    /**
     * Updates the list of lobbies with the provided new lobbies.
     *
     * @param newLobbies A list of updated lobby information.
     */
    public static synchronized void updateLobbies(List<Lobby> newLobbies) {
        lobbiesMap.clear();
        for (Lobby lobby : newLobbies) {
            lobbiesMap.put(lobby.getId(), lobby);
        }
        Platform.runLater(() -> {
            lobbiesList.setAll(newLobbies);
        });
    }

    /**
     * Gets the current lobby.
     *
     * @return The current lobby.
     */
    public static Lobby getCurrentLobby() {
        synchronized (currentLobbyLock) {
            return currentLobby;
        }
    }

    /**
     * Sets the current lobby to the specified lobby.
     *
     * @param lobby The lobby to set as the current lobby.
     */
    public static void setCurrentLobby(Lobby lobby) {
        synchronized (currentLobbyLock) {
            currentLobby = lobby;
        }
    }

    /**
     * Gets a lobby by its ID.
     *
     * @param id The ID of the lobby to retrieve.
     * @return The lobby with the specified ID, or null if not found.
     */
    public static synchronized Lobby getLobby(int id) {
        return lobbiesMap.get(id);
    }

    /**
     * Adds a new lobby to the manager.
     *
     * @param lobby The lobby to add.
     */
    public static synchronized void addLobby(Lobby lobby) {
        lobbiesMap.put(lobby.getId(), lobby);
        Platform.runLater(() -> {
            lobbiesList.add(lobby);
        });
    }

    /**
     * Removes a lobby from the manager by its ID.
     *
     * @param id The ID of the lobby to remove.
     */
    public static synchronized void removeLobby(int id) {
        lobbiesMap.remove(id);
        Platform.runLater(() -> {
            lobbiesList.removeIf(lobby -> lobby.getId() == id);
        });
    }

    /**
     * Gets the list of lobbies.
     *
     * @return The list of lobbies.
     */
    public static ObservableList<Lobby> getLobbiesList() {
        return lobbiesList;
    }

    /**
     * Updates the current lobby with new lobby information, including users and game-related data.
     *
     * @param lobbyInfoToUpdate The updated lobby information.
     */
    public static void updateCurrentLobby(Lobby lobbyInfoToUpdate) {
        Lobby currentLobby = LobbyManager.getCurrentLobby();
        currentLobby.setAdminInfo(lobbyInfoToUpdate.getAdminInfo());
        currentLobby.setCurrentPlayers(lobbyInfoToUpdate.getCurrentPlayers());
        currentLobby.updateUsersList(lobbyInfoToUpdate.getUsersList());
        if (currentLobby.getGameObject() != null) {
            currentLobby.getGameObject().updatePlayers(lobbyInfoToUpdate.getGameObject().getPlayers(), true);
        }
    }

    /**
     * Updates the current lobby with new lobby information, excluding the application user.
     *
     * @param lobbyInfoToUpdate The updated lobby information.
     */
    public static void updateCurrentLobbyWithoutUpdateApplicationUser(Lobby lobbyInfoToUpdate) {
        Lobby currentLobby = LobbyManager.getCurrentLobby();
        currentLobby.setAdminInfo(lobbyInfoToUpdate.getAdminInfo());
        currentLobby.setCurrentPlayers(lobbyInfoToUpdate.getCurrentPlayers());
        currentLobby.updateUsersList(lobbyInfoToUpdate.getUsersList());
        if (currentLobby.getGameObject() != null) {
            currentLobby.getGameObject().updatePlayers(lobbyInfoToUpdate.getGameObject().getPlayers(), false);
        }
    }

    /**
     * Marks a user as disconnected in the current lobby's game.
     *
     * @param user The user to mark as disconnected.
     */
    public static void markUserAsDisconnect(User user) {
        Lobby currentLobby = LobbyManager.getCurrentLobby();

        if (currentLobby == null) {
            return;
        }

        if (currentLobby.getGameObject() == null) {
            return;
        }

        currentLobby.getGameObject().getPlayers().forEach(player -> {
            if (player.getUsername().equals(user.getUsername())) {
                player.setOnline(false);
            }
        });
    }

}

