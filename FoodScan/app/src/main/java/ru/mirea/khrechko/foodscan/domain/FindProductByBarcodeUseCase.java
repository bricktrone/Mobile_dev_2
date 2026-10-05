package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;

public class FindProductByBarcodeUseCase {

    private final ProductRepository productRepository;

    public FindProductByBarcodeUseCase(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public Product execute(String barcode) {

        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }

        return productRepository.findByBarcode(
                barcode.trim()
        );
    }
}
