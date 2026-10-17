package ru.mirea.khrechko.foodscan.data.storage;

public class UserStorageModel {
    private final String uid;
    private final String email;
    private final String displayName;
    private final long lastLoginMillis;

    public UserStorageModel(String uid, String email, String displayName, long lastLoginMillis) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.lastLoginMillis = lastLoginMillis;
    }
    public String getUid() { return uid; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public long getLastLoginMillis() { return lastLoginMillis; }
}
