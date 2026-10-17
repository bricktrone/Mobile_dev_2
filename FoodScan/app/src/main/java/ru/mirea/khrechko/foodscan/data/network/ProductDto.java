package ru.mirea.khrechko.foodscan.data.network;

public class ProductDto {
    public final int id;
    public final String barcode;
    public final String name;
    public final String brand;
    public final String imageUrl;
    public final String ingredients;
    public final int calories;

    public ProductDto(int id, String barcode, String name, String brand,
                      String imageUrl, String ingredients, int calories) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.calories = calories;
    }
}
