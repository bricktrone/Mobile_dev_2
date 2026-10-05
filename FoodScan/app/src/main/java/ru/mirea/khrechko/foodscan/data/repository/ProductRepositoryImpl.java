package ru.mirea.khrechko.foodscan.data.repository;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;

public class ProductRepositoryImpl implements ProductRepository {

    private final List<Product> catalog;

    public ProductRepositoryImpl(Context context) {

        catalog = new ArrayList<>();

        catalog.add(
                new Product(
                        1,
                        "Молоко",
                        "Простоквашино",
                        "https://example.com/milk.jpg",
                        "Молоко нормализованное",
                        60
                )
        );

        catalog.add(
                new Product(
                        2,
                        "Банан",
                        "FoodScan",
                        "https://example.com/banana.jpg",
                        "Банан",
                        89
                )
        );

        catalog.add(
                new Product(
                        3,
                        "Яблоко",
                        "FoodScan",
                        "https://example.com/apple.jpg",
                        "Яблоко",
                        52
                )
        );

        catalog.add(
                new Product(
                        4,
                        "Шоколад",
                        "Alpen Gold",
                        "https://example.com/chocolate.jpg",
                        "Какао, сахар, сухое молоко",
                        535
                )
        );

        catalog.add(
                new Product(
                        5,
                        "Хлеб",
                        "Хлебный дом",
                        "https://example.com/bread.jpg",
                        "Мука пшеничная, вода, соль, дрожжи",
                        250
                )
        );
    }

    @Override
    public List<Product> getCatalog() {
        return catalog;
    }

    @Override
    public Product getProductById(int id) {

        for (Product product : catalog) {

            if (product.getId() == id) {
                return product;
            }
        }

        return null;
    }

    @Override
    public Product findByBarcode(String barcode) {

        if (barcode == null) {
            return null;
        }

        // Тестовые штрихкоды
        switch (barcode) {

            case "460000000001":
                return catalog.get(0);

            case "460000000002":
                return catalog.get(1);

            case "460000000003":
                return catalog.get(2);

            case "460000000004":
                return catalog.get(3);

            case "460000000005":
                return catalog.get(4);

            default:
                return null;
        }
    }
}