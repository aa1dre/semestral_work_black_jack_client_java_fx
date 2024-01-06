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


    // Синхронизированные методы для работы с картой лобби
    public static synchronized void updateLobbies(List<Lobby> newLobbies) {
        lobbiesMap.clear();
        for (Lobby lobby : newLobbies) {
            lobbiesMap.put(lobby.getId(), lobby);
        }
        Platform.runLater(() -> {
            lobbiesList.setAll(newLobbies);
        });
    }

    public static Lobby getCurrentLobby() {
        synchronized (currentLobbyLock) {
            return currentLobby;
        }
    }

    public static void setCurrentLobby(Lobby lobby) {
        synchronized (currentLobbyLock) {
            currentLobby = lobby;
        }
    }

    public static synchronized Lobby getLobby(int id) {
        return lobbiesMap.get(id);
    }

    public static synchronized void addLobby(Lobby lobby) {
        lobbiesMap.put(lobby.getId(), lobby);
        Platform.runLater(() -> {
            lobbiesList.add(lobby);
        });
    }

    public static synchronized void removeLobby(int id) {
        lobbiesMap.remove(id);
        Platform.runLater(() -> {
            lobbiesList.removeIf(lobby -> lobby.getId() == id);
        });
    }

    public static ObservableList<Lobby> getLobbiesList() {
        return lobbiesList;
    }

}
