package ru.mirea.khrechko.foodscan.domain;

import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;

public class GetProductDetailsUseCase {

    private final ProductRepository productRepository;

    public GetProductDetailsUseCase(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public Product execute(int productId) {

        if (productId <= 0) {
            return null;
        }

        return productRepository.getProductById(productId);
    }
}
