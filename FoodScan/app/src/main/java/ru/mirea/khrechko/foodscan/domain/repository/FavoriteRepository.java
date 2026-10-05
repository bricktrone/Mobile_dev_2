package ru.mirea.khrechko.foodscan.domain.repository;

import java.util.List;

import ru.mirea.khrechko.foodscan.domain.models.Product;

public interface FavoriteRepository {

    boolean add(int userId, Product product);

    boolean remove(int userId, Product product);

    List<Product> getFavorites(int userId);
}