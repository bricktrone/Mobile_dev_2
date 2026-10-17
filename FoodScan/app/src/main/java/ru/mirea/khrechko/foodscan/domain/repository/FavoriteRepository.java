package ru.mirea.khrechko.foodscan.domain.repository;

import java.util.List;
import ru.mirea.khrechko.foodscan.domain.models.Product;

public interface FavoriteRepository {
    boolean add(String userId, Product product);
    boolean remove(String userId, Product product);
    List<Product> getFavorites(String userId);
}
