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

    /**
     * Creates a new User instance with the specified username and password.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     */
    public User(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    /**
     * Creates a new User instance with the specified username, password, name, and surname.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param name     The name of the user.
     * @param surname  The surname of the user.
     */
    public User(String username, String password, String name, String surname) {
        setUsername(username);
        setPassword(password);
        setName(name);
        setSurname(surname);
    }

    /**
     * Creates a new User instance with the specified username, name, surname, and online status.
     *
     * @param username  The username of the user.
     * @param name      The name of the user.
     * @param surname   The surname of the user.
     * @param isOnline  The online status of the user.
     */
    public User(String username, String name, String surname, boolean isOnline) {
        setUsername(username);
        setName(name);
        setSurname(surname);
        setOnline(isOnline);
    }

    /**
     * Creates a new User instance with the specified username, name, and surname.
     *
     * @param username The username of the user.
     * @param name     The name of the user.
     * @param surname  The surname of the user.
     */
    public User(String username, String name, String surname) {
        setUsername(username);
        setName(name);
        setSurname(surname);
    }

    /**
     * Returns a string representation of the user for login purposes.
     *
     * @return A string containing the username and password.
     */
    public String toStringLogin() {
        return getUsername() + ";" + getPassword();
    }

    /**
     * Returns a string representation of the user for registration purposes.
     *
     * @return A string containing the username, password, name, and surname.
     */
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

    /**
     * Compares this User object to another object for equality.
     *
     * @param obj The object to compare with.
     * @return True if the objects are equal; otherwise, false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User user = (User) obj;
        return username != null ? username.get().equals(user.getUsername()) : user.getUsername() == null;
    }

    /**
     * Computes the hash code for this User object.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        return username != null ? username.get().hashCode() : 0;
    }

    /**
     * Returns a string representation of the user, showing the username, name, and surname.
     *
     * @return A string containing the username - name, and surname.
     */
    @Override
    public String toString() {
        return username.get() + " - " + name.get() + " " + surname.get();
    }
}
