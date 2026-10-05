package ru.mirea.khrechko.foodscan.domain.models;

public class Product {

    private int id;
    private String name;
    private String brand;
    private String imageUrl;
    private String ingredients;
    private int calories;

    public Product(
            int id,
            String name,
            String brand,
            String imageUrl,
            String ingredients,
            int calories
    ) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.calories = calories;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getIngredients() {
        return ingredients;
    }

    public int getCalories() {
        return calories;
    }
}
