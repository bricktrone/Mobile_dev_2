package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

public class LoginUseCase {
    private final AuthRepository authRepository;
    public LoginUseCase(AuthRepository authRepository) { this.authRepository = authRepository; }

    public boolean execute(String email, String password) {
        if (email == null || !email.trim().contains("@")) return false;
        if (password == null || password.isEmpty()) return false;
        return authRepository.login(email.trim(), password);
    }
}
