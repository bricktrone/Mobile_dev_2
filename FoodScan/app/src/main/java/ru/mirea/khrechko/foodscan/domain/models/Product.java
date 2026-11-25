package ru.mirea.khrechko.foodscan.domain.models;

public class Product {

    private int id;
    private String name;
    private String brand;
    private String imageUrl;
    private String ingredients;
    private int calories;
    private double proteins;
    private double fat;

    public Product(int id, String name, String brand, String imageUrl,
                   String ingredients, int calories) {
        this(id, name, brand, imageUrl, ingredients, calories, -1, -1);
    }

    public Product(int id, String name, String brand, String imageUrl,
                   String ingredients, int calories, double proteins, double fat) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.imageUrl = imageUrl;
        this.ingredients = ingredients;
        this.calories = calories;
        this.proteins = proteins;
        this.fat = fat;
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

    /** Grams per 100 g; -1 means that Open Food Facts has no value. */
    public double getProteins() { return proteins; }

    /** Grams per 100 g; -1 means that Open Food Facts has no value. */
    public double getFat() { return fat; }
}
