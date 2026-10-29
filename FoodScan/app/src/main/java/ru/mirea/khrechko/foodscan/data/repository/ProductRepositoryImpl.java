package ru.mirea.khrechko.foodscan.data.repository;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.khrechko.foodscan.data.network.NetworkApi;
import ru.mirea.khrechko.foodscan.data.network.ProductDto;
import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;

public class ProductRepositoryImpl implements ProductRepository {
    private final NetworkApi networkApi;

    public ProductRepositoryImpl(NetworkApi networkApi) {
        this.networkApi = networkApi;
    }

    /** Maps the remote Open Food Facts DTOs into domain entities. */
    @Override
    public List<Product> getCatalog() {
        List<Product> result = new ArrayList<>();
        for (ProductDto dto : networkApi.getProducts()) result.add(mapToDomain(dto));
        return result;
    }

    @Override
    public Product getProductById(int id) {
        ProductDto dto = networkApi.getProductById(id);
        return dto == null ? null : mapToDomain(dto);
    }

    @Override
    public Product findByBarcode(String barcode) {
        ProductDto dto = networkApi.getProductByBarcode(barcode);
        return dto == null ? null : mapToDomain(dto);
    }

    private Product mapToDomain(ProductDto dto) {
        return new Product(dto.id, dto.name, dto.brand, dto.imageUrl, dto.ingredients, dto.calories);
    }
}
