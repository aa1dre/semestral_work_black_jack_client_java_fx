package com.aakhramchuk.clientfx.objects;

public class User {
    private String username;
    private String password;
    private String name;
    private String surname;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, String name, String surname) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }

    public String toStringLogin() {
        return username + ";" + password; // Добавляем символ конца строки
    }

    public String toStringRegistration() {
        return username + ";" + password + ";" + name + ";" + surname;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
