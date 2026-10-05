package ru.mirea.khrechko.foodscan.domain;

import java.util.List;

import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;

public class GetProductCatalogUseCase {

    private final ProductRepository productRepository;

    public GetProductCatalogUseCase(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public List<Product> execute() {
        return productRepository.getCatalog();
    }
}
