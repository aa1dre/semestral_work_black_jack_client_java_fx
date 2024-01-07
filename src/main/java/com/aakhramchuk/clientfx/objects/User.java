package com.aakhramchuk.clientfx.objects;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty surname = new SimpleStringProperty();
    private BooleanProperty isCreator = new SimpleBooleanProperty();
    private BooleanProperty isAdmin = new SimpleBooleanProperty();
    private BooleanProperty isOnline = new SimpleBooleanProperty();

    public User(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    @Override
    public String toString() {
        return username + " - " + name + " " + surname;
    }

    public User(String username, String password, String name, String surname) {
        setUsername(username);
        setPassword(password);
        setName(name);
        setSurname(surname);
    }

    public User(String name, String surname, String username) {
        setName(name);
        setSurname(surname);
        setUsername(username);
    }

    public String toStringLogin() {
        return getUsername() + ";" + getPassword();
    }

    public String toStringRegistration() {
        return getUsername() + ";" + getPassword() + ";" + getName() + ";" + getSurname();
    }

    // Username property
    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    // Password property
    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }
    public StringProperty passwordProperty() { return password; }

    // Name property
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    // Surname property
    public String getSurname() { return surname.get(); }
    public void setSurname(String surname) { this.surname.set(surname); }
    public StringProperty surnameProperty() { return surname; }

    // isCreator property
    public boolean isCreator() { return isCreator.get(); }
    public void setCreator(boolean isCreator) { this.isCreator.set(isCreator); }
    public BooleanProperty creatorProperty() { return isCreator; }

    // isAdmin property
    public boolean isAdmin() { return isAdmin.get(); }
    public void setAdmin(boolean isAdmin) { this.isAdmin.set(isAdmin); }
    public BooleanProperty adminProperty() { return isAdmin; }

    // isOnline property
    public boolean isOnline() { return isOnline.get(); }
    public void setOnline(boolean isOnline) { this.isOnline.set(isOnline); }
    public BooleanProperty onlineProperty() { return isOnline; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User user = (User) obj;
        return username != null ? username.get().equals(user.getUsername()) : user.getUsername() == null;
    }

    @Override
    public int hashCode() {
        return username != null ? username.get().hashCode() : 0;
    }
}
