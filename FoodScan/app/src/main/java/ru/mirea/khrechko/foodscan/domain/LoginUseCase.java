package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

public class LoginUseCase {
    private final AuthRepository authRepository;

    public LoginUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public boolean execute(String login, String password) {

        if (login == null || login.trim().isEmpty()) {
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        return authRepository.login(login, password);
    }
}
