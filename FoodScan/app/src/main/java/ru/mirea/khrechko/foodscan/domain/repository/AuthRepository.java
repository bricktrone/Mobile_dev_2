package ru.mirea.khrechko.foodscan.domain.repository;

import ru.mirea.khrechko.foodscan.domain.models.User;

public interface AuthRepository {

    boolean login(String login, String password);

    boolean register(String login, String password);

    User getProfile();

    void logout();
}