package ru.mirea.khrechko.foodscan.domain;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;

public class GetFavoritesUseCase {

    private final AuthRepository authRepository;
    private final FavoriteRepository favoriteRepository;

    public GetFavoritesUseCase(
            AuthRepository authRepository,
            FavoriteRepository favoriteRepository
    ) {
        this.authRepository = authRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public List<Product> execute() {

        User user = authRepository.getProfile();

        if (user == null || user.isGuest()) {
            return new ArrayList<>();
        }

        return favoriteRepository.getFavorites(
                user.getId()
        );
    }
}
