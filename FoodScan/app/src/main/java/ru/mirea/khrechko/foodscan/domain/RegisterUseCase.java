package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

public class RegisterUseCase {
    private final AuthRepository authRepository;

    public RegisterUseCase(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public boolean execute(String login, String password) {

        if (login == null || login.trim().isEmpty()) {
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        if (password.length() < 4) {
            return false;
        }

        return authRepository.register(login, password);
    }
}
