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

    @Override
    public List<Product> getCatalog() {
        List<Product> result = new ArrayList<>();
        for (ProductDto dto : networkApi.getProducts()) result.add(mapToDomain(dto));
        return result;
    }

    @Override
    public Product getProductById(int id) {
        for (ProductDto dto : networkApi.getProducts()) {
            if (dto.id == id) return mapToDomain(dto);
        }
        return null;
    }

    @Override
    public Product findByBarcode(String barcode) {
        if (barcode == null) return null;
        String target = barcode.trim();
        for (ProductDto dto : networkApi.getProducts()) {
            if (dto.barcode.equals(target)) return mapToDomain(dto);
        }
        return null;
    }

    private Product mapToDomain(ProductDto dto) {
        return new Product(dto.id, dto.name, dto.brand, dto.imageUrl, dto.ingredients, dto.calories);
    }
}
