package ru.mirea.khrechko.foodscan.domain.repository;

import java.util.List;

import ru.mirea.khrechko.foodscan.domain.models.Product;

public interface ProductRepository {

    List<Product> getCatalog();

    Product getProductById(int id);

    Product findByBarcode(String barcode);
}