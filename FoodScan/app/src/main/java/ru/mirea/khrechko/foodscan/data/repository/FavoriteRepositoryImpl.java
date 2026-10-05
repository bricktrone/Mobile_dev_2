package ru.mirea.khrechko.foodscan.data.repository;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;

public class FavoriteRepositoryImpl implements FavoriteRepository {

    private final Map<Integer, List<Product>> favorites;

    public FavoriteRepositoryImpl(Context context) {
        favorites = new HashMap<>();
    }

    @Override
    public boolean add(int userId, Product product) {

        if (product == null) {
            return false;
        }

        List<Product> userFavorites =
                favorites.get(userId);

        if (userFavorites == null) {
            userFavorites = new ArrayList<>();
            favorites.put(userId, userFavorites);
        }

        for (Product item : userFavorites) {

            if (item.getId() == product.getId()) {
                return false;
            }
        }

        userFavorites.add(product);

        return true;
    }

    @Override
    public boolean remove(int userId, Product product) {

        if (product == null) {
            return false;
        }

        List<Product> userFavorites =
                favorites.get(userId);

        if (userFavorites == null) {
            return false;
        }

        return userFavorites.removeIf(
                item -> item.getId() == product.getId()
        );
    }

    @Override
    public List<Product> getFavorites(int userId) {

        List<Product> userFavorites =
                favorites.get(userId);

        if (userFavorites == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(userFavorites);
    }
}