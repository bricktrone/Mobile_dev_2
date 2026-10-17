package ru.mirea.khrechko.foodscan.data.room;

import androidx.room.Entity;
import androidx.annotation.NonNull;

@Entity(tableName = "favorite_products", primaryKeys = {"userId", "productId"})
public class FavoriteProductEntity {
    @NonNull
    public String userId;
    public int productId;
    public String name;
    public String brand;
    public String imageUrl;
    public String ingredients;
    public int calories;

    public FavoriteProductEntity(@NonNull String userId, int productId, String name, String brand,
                                 String imageUrl, String ingredients, int calories) {
        this.userId = userId;
        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.calories = calories;
    }
}
