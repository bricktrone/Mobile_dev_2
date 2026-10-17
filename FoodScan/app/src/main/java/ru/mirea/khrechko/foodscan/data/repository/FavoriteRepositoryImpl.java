package ru.mirea.khrechko.foodscan.data.repository;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.khrechko.foodscan.data.room.FavoriteProductDao;
import ru.mirea.khrechko.foodscan.data.room.FavoriteProductEntity;
import ru.mirea.khrechko.foodscan.data.room.FoodScanDatabase;
import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;

public class FavoriteRepositoryImpl implements FavoriteRepository {
    private final FavoriteProductDao dao;

    public FavoriteRepositoryImpl(FoodScanDatabase database) {
        dao = database.favoriteProductDao();
    }

    @Override
    public boolean add(String userId, Product product) {
        if (isEmpty(userId) || product == null) return false;
        if (dao.getByUserAndProduct(userId, product.getId()) != null) return false;
        dao.insert(mapToEntity(userId, product));
        return true;
    }

    @Override
    public boolean remove(String userId, Product product) {
        if (isEmpty(userId) || product == null) return false;
        return dao.delete(userId, product.getId()) > 0;
    }

    @Override
    public List<Product> getFavorites(String userId) {
        List<Product> result = new ArrayList<>();
        if (isEmpty(userId)) return result;
        for (FavoriteProductEntity entity : dao.getByUserId(userId)) {
            result.add(mapToDomain(entity));
        }
        return result;
    }

    private boolean isEmpty(String value) { return value == null || value.trim().isEmpty(); }

    private FavoriteProductEntity mapToEntity(String userId, Product product) {
        return new FavoriteProductEntity(userId, product.getId(), product.getName(),
                product.getBrand(), product.getImageUrl(), product.getIngredients(), product.getCalories());
    }

    private Product mapToDomain(FavoriteProductEntity entity) {
        return new Product(entity.productId, entity.name, entity.brand, entity.imageUrl,
                entity.ingredients, entity.calories);
    }
}
