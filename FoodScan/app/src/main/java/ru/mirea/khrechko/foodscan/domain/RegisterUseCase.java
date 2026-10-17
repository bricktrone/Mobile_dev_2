package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;

public class RegisterUseCase {
    private static final int MIN_PASSWORD_LENGTH = 6;
    private final AuthRepository authRepository;
    public RegisterUseCase(AuthRepository authRepository) { this.authRepository = authRepository; }

    public boolean execute(String email, String password, String displayName) {
        if (email == null || !email.trim().contains("@")) return false;
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) return false;
        return authRepository.register(email.trim(), password, displayName);
    }
}
