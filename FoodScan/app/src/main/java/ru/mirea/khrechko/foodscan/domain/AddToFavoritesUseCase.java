package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;

public class AddToFavoritesUseCase {

    private final AuthRepository authRepository;
    private final FavoriteRepository favoriteRepository;

    public AddToFavoritesUseCase(
            AuthRepository authRepository,
            FavoriteRepository favoriteRepository
    ) {
        this.authRepository = authRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public boolean execute(Product product) {

        User user = authRepository.getProfile();

        if (user == null || user.isGuest()) {
            return false;
        }

        if (product == null) {
            return false;
        }

        return favoriteRepository.add(
                user.getId(),
                product
        );
    }
}
