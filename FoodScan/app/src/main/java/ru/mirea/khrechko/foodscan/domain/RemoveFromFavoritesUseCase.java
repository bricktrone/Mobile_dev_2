package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;

public class RemoveFromFavoritesUseCase {

    private final AuthRepository authRepository;
    private final FavoriteRepository favoriteRepository;

    public RemoveFromFavoritesUseCase(
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

        return favoriteRepository.remove(
                user.getId(),
                product
        );
    }
}
