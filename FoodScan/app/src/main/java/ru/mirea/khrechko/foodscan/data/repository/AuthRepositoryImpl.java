package ru.mirea.khrechko.foodscan.data.repository;

import ru.mirea.khrechko.foodscan.data.auth.AuthSourceException;
import ru.mirea.khrechko.foodscan.data.auth.AuthUserDto;
import ru.mirea.khrechko.foodscan.data.auth.FirebaseAuthSource;
import ru.mirea.khrechko.foodscan.data.storage.UserStorage;
import ru.mirea.khrechko.foodscan.data.storage.UserStorageModel;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuthSource authSource;
    private final UserStorage userStorage;
    private volatile String lastError = "";

    public AuthRepositoryImpl(FirebaseAuthSource authSource, UserStorage userStorage) {
        this.authSource = authSource;
        this.userStorage = userStorage;
    }

    @Override
    public boolean login(String email, String password) {
        lastError = "";
        try {
            AuthUserDto user = authSource.signIn(email.trim(), password);
            userStorage.save(mapToStorage(user));
            lastError = "";
            return true;
        } catch (AuthSourceException e) {
            lastError = e.getMessage();
            return false;
        }
    }

    @Override
    public boolean register(String email, String password, String displayName) {
        lastError = "";
        try {
            AuthUserDto user = authSource.register(email.trim(), password, displayName);
            userStorage.save(mapToStorage(user));
            lastError = "";
            return true;
        } catch (AuthSourceException e) {
            lastError = e.getMessage();
            return false;
        }
    }

    @Override
    public User getProfile() {
        AuthUserDto current = authSource.getCurrentUser();
        if (current == null) return new User(null, "Гость", true);

        UserStorageModel stored = userStorage.get();
        if (stored == null || !current.getUid().equals(stored.getUid())) {
            stored = mapToStorage(current);
            userStorage.save(stored);
        }
        String name = stored.getDisplayName();
        if (name == null || name.trim().isEmpty()) name = stored.getEmail();
        return new User(current.getUid(), name, false);
    }

    @Override
    public void logout() {
        authSource.signOut();
        userStorage.clear();
        lastError = "";
    }

    @Override
    public String getLastError() { return lastError; }

    private UserStorageModel mapToStorage(AuthUserDto user) {
        return new UserStorageModel(user.getUid(), user.getEmail(), user.getDisplayName(),
                System.currentTimeMillis());
    }

}
