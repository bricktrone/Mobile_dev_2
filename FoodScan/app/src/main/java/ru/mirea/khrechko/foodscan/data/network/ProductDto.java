package ru.mirea.khrechko.foodscan.data.network;

public class ProductDto {
    public final int id;
    public final String barcode;
    public final String name;
    public final String brand;
    public final String imageUrl;
    public final String ingredients;
    public final int calories;
    public final double proteins;
    public final double fat;

    public ProductDto(int id, String barcode, String name, String brand,
                      String imageUrl, String ingredients, int calories) {
        this(id, barcode, name, brand, imageUrl, ingredients, calories, -1, -1);
    }

    public ProductDto(int id, String barcode, String name, String brand,
                      String imageUrl, String ingredients, int calories,
                      double proteins, double fat) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.calories = calories;
        this.proteins = proteins;
        this.fat = fat;
    }
}
