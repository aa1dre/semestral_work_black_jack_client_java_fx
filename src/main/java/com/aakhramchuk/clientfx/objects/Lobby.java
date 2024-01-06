package com.aakhramchuk.clientfx.objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Lobby {
    private IntegerProperty idP = new SimpleIntegerProperty();
    private StringProperty name = new SimpleStringProperty();
    private IntegerProperty maxPlayers = new SimpleIntegerProperty();
    private BooleanProperty hasPassword = new SimpleBooleanProperty();
    private IntegerProperty currentPlayers = new SimpleIntegerProperty();
    private StringProperty adminInfo = new SimpleStringProperty();
    private StringProperty creatorInfo = new SimpleStringProperty();
    private BooleanProperty gameStarted = new SimpleBooleanProperty();
    private GameObject gameObject; // Assuming GameObject does not need to be a property.

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

    public String toActionString(String password) {
        return getName() + ";" + (getHasPassword() ? "1" : "0") + (password == null || password.isEmpty() ? "" : ";" + password);
    }

    public static String toCreateString(String name, int maxPlayers, boolean hasPassword, String password) {
        return name + ";" + maxPlayers + ";" + (hasPassword ? "1" : "0") + (password == null || password.isEmpty() ? "" : ";" + password);
    }

    public static String toCreateStringWithoutPassword(String name, int maxPlayers) {
        return toCreateString(name, maxPlayers, false, null);
    }

    public boolean hasPassword() {
        return getHasPassword();
    }
}
