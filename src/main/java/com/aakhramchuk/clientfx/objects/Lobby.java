package com.aakhramchuk.clientfx.objects;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Lobby {
    private IntegerProperty idP = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private IntegerProperty maxPlayers = new SimpleIntegerProperty();
    private BooleanProperty hasPassword = new SimpleBooleanProperty();
    private IntegerProperty currentPlayers = new SimpleIntegerProperty();
    private StringProperty adminInfo = new SimpleStringProperty();
    private StringProperty creatorInfo = new SimpleStringProperty();
    private final ObservableList<User> usersList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private BooleanProperty gameStarted = new SimpleBooleanProperty();
    private GameObject gameObject;


    /**
     * Initializes a new Lobby object with the specified properties.
     *
     * @param id             The unique identifier of the lobby.
     * @param name           The name of the lobby.
     * @param maxPlayers     The maximum number of players allowed in the lobby.
     * @param hasPassword    A boolean indicating whether the lobby is password-protected.
     * @param currentPlayers The current number of players in the lobby.
     * @param adminInfo      Information about the lobby's admin.
     * @param creatorInfo    Information about the creator of the lobby.
     * @param gameStarted    A boolean indicating whether the game in the lobby has started.
     */
    public Lobby(int id, String name, int maxPlayers, boolean hasPassword,
                 int currentPlayers, String adminInfo, String creatorInfo, boolean gameStarted) {
        setId(id);
        setName(name);
        setMaxPlayers(maxPlayers);
        setHasPassword(hasPassword);
        setCurrentPlayers(currentPlayers);
        setAdminInfo(adminInfo);
        setCreatorInfo(creatorInfo);
        setGameStarted(gameStarted);
    }

    /**
     * Initializes a new Lobby object with default values and specified properties.
     *
     * @param name        The name of the lobby.
     * @param maxPlayers  The maximum number of players allowed in the lobby.
     * @param hasPassword A boolean indicating whether the lobby is password-protected.
     */
    public Lobby(String name, int maxPlayers, boolean hasPassword) {
        this(0, name, maxPlayers, hasPassword, 0, "", "", false);
    }

    // ID property
    public int getId() { return idP.get(); }
    public void setId(int id) { idP.set(id); }
    public IntegerProperty idProperty() { return idP; }

    // Name property
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    // Max players property
    public int getMaxPlayers() { return maxPlayers.get(); }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers.set(maxPlayers); }
    public IntegerProperty maxPlayersProperty() { return maxPlayers; }

    // Has password property
    public boolean getHasPassword() { return hasPassword.get(); }
    public void setHasPassword(boolean hasPassword) { this.hasPassword.set(hasPassword); }
    public BooleanProperty hasPasswordProperty() { return hasPassword; }

    // Current players property
    public int getCurrentPlayers() { return currentPlayers.get(); }
    public void setCurrentPlayers(int currentPlayers) { this.currentPlayers.set(currentPlayers); }
    public IntegerProperty currentPlayersProperty() { return currentPlayers; }

    // Admin info property
    public String getAdminInfo() { return adminInfo.get(); }
    public void setAdminInfo(String adminInfo) { this.adminInfo.set(adminInfo); }
    public StringProperty adminInfoProperty() { return adminInfo; }

    // Creator info property
    public String getCreatorInfo() { return creatorInfo.get(); }
    public void setCreatorInfo(String creatorInfo) { this.creatorInfo.set(creatorInfo); }
    public StringProperty creatorInfoProperty() { return creatorInfo; }

    // Game started property
    public boolean getGameStarted() { return gameStarted.get(); }
    public void setGameStarted(boolean gameStarted) { this.gameStarted.set(gameStarted); }
    public BooleanProperty gameStartedProperty() { return gameStarted; }

    // Other methods
    public GameObject getGameObject() { return gameObject; }
    public void setGameObject(GameObject gameObject) { this.gameObject = gameObject; }


    /**
     * Generates an action string representing lobby information for a client action, including password if provided.
     *
     * @param password The lobby password (if applicable).
     * @return The formatted action string.
     */
    public String toActionString(String password) {
        return getName() + ";" + (getHasPassword() ? "1" : "0") + (password == null || password.isEmpty() ? "" : ";" + password);
    }

    /**
     * Generates a formatted lobby creation string.
     *
     * @param name       The name of the lobby.
     * @param maxPlayers The maximum number of players allowed in the lobby.
     * @param hasPassword Indicates whether the lobby has a password.
     * @param password   The lobby's password (null or empty if no password is set).
     * @return The formatted lobby creation string.
     */
    public static String toCreateString(String name, int maxPlayers, boolean hasPassword, String password) {
        return name + ";" + maxPlayers + ";" + (hasPassword ? "1" : "0") + (password == null || password.isEmpty() ? "" : ";" + password);
    }

    /**
     * Checks if the lobby has a password.
     *
     * @return true if the lobby has a password, false otherwise.
     */
    public static String toCreateStringWithoutPassword(String name, int maxPlayers) {
        return toCreateString(name, maxPlayers, false, null);
    }

    /**
     * Checks if the lobby has a password.
     *
     * @return true if the lobby has a password, false otherwise.
     */
    public boolean hasPassword() {
        return getHasPassword();
    }

    /**
     * Updates the lobby's user list with the provided list of users.
     *
     * @param users The list of users to update the lobby's user list.
     */
    public void updateUsersList(ObservableList<User> users) {
        usersList.clear();
        usersList.addAll(users);
    }

    /**
     * Adds a user to the lobby's user list.
     *
     * @param user The user to add to the lobby.
     */
    public void addUser(User user) {
        usersList.add(user);
    }

    /**
     * Retrieves the list of users in the lobby.
     *
     * @return The observable list of users in the lobby.
     */
    public ObservableList<User> getUsersList() {
        return usersList;
    }

}
