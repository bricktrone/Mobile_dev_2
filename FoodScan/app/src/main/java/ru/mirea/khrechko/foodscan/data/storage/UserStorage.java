package ru.mirea.khrechko.foodscan.data.storage;

public interface UserStorage {
    UserStorageModel get();
    boolean save(UserStorageModel user);
    void clear();
}
