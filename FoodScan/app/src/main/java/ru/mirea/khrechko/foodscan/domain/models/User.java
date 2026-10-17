package ru.mirea.khrechko.foodscan.domain.models;

public class User {
    private final String id;
    private final String login;
    private final boolean guest;

    public User(String id, String login, boolean guest) {
        this.id = id;
        this.login = login;
        this.guest = guest;
    }

    public String getId() { return id; }
    public String getLogin() { return login; }
    public boolean isGuest() { return guest; }
}
