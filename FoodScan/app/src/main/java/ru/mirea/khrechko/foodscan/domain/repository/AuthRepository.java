package ru.mirea.khrechko.foodscan.domain.repository;

import ru.mirea.khrechko.foodscan.domain.models.User;

public interface AuthRepository {
    boolean login(String email, String password);
    boolean register(String email, String password, String displayName);
    User getProfile();
    void logout();
    String getLastError();
}
