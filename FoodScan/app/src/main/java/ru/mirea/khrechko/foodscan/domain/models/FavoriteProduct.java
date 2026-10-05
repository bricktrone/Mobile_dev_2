package ru.mirea.khrechko.foodscan.domain.models;

public class FavoriteProduct {

    private int userId;
    private int productId;

    public FavoriteProduct(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public int getProductId() {
        return productId;
    }
}
