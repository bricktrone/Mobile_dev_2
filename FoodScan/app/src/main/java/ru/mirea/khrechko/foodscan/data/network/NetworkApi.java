package ru.mirea.khrechko.foodscan.data.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Retrofit client for the Open Food Facts JSON API. */
public class NetworkApi {
    private static final String BASE_URL = "https://world.openfoodfacts.org/";
    private static final String FIELDS = "code,product_name,brands,image_front_url,ingredients_text,nutriments";
    private static final int PAGE_SIZE = 30;

    private final OpenFoodFactsApi api;
    private volatile List<ProductDto> cachedProducts;
    private final Map<Integer, ProductDto> cachedById = new ConcurrentHashMap<>();
    private final Map<String, ProductDto> cachedByBarcode = new ConcurrentHashMap<>();

    public NetworkApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(OpenFoodFactsApi.class);
    }

    /**
     * Synchronous calls must be invoked from the repository's background executor.
     * Successful results are cached for detail screens and subsequent barcode lookups.
     */
    public List<ProductDto> getProducts() {
        List<ProductDto> snapshot = cachedProducts;
        if (snapshot != null) return new ArrayList<>(snapshot);

        synchronized (this) {
            if (cachedProducts != null) return new ArrayList<>(cachedProducts);
            final Response<OpenFoodFactsApi.SearchResponse> response;
            try {
                response = api.searchProducts("russia", 1, PAGE_SIZE, FIELDS).execute();
            } catch (IOException e) {
                throw new NetworkException(
                        "Нет подключения к Open Food Facts. Проверьте интернет и повторите попытку.", e);
            } catch (RuntimeException e) {
                throw new NetworkException("Не удалось обработать ответ Open Food Facts.", e);
            }

            if (!response.isSuccessful()) {
                throw new NetworkException("Open Food Facts ответил ошибкой HTTP " + response.code() + ".");
            }
            OpenFoodFactsApi.SearchResponse body = response.body();
            if (body == null || body.products == null) {
                throw new NetworkException("Open Food Facts вернул пустой или некорректный JSON.");
            }

            List<ProductDto> products = new ArrayList<>();
            for (OpenFoodFactsApi.OpenFoodFactsProduct item : body.products) {
                ProductDto dto = mapProduct(item);
                if (dto != null) {
                    products.add(dto);
                    cache(dto);
                }
            }
            if (products.isEmpty()) {
                throw new NetworkException("В ответе Open Food Facts не найдено продуктов с необходимыми полями.");
            }
            cachedProducts = Collections.unmodifiableList(new ArrayList<>(products));
            return new ArrayList<>(cachedProducts);
        }
    }

    /** Looks up a barcode directly, not only in the first page of the catalog. */
    public ProductDto getProductByBarcode(String barcode) {
        if (isBlank(barcode)) return null;
        String normalized = barcode.trim();
        ProductDto cached = cachedByBarcode.get(normalized);
        if (cached != null) return cached;

        final Response<OpenFoodFactsApi.ProductResponse> response;
        try {
            response = api.getProductByBarcode(normalized, FIELDS).execute();
        } catch (IOException e) {
            throw new NetworkException("Нет подключения к Open Food Facts. Проверьте интернет.", e);
        } catch (RuntimeException e) {
            throw new NetworkException("Не удалось обработать ответ Open Food Facts.", e);
        }
        if (!response.isSuccessful()) {
            throw new NetworkException("Open Food Facts ответил ошибкой HTTP " + response.code() + ".");
        }
        OpenFoodFactsApi.ProductResponse body = response.body();
        if (body == null || body.status != 1) return null;
        ProductDto dto = mapProduct(body.product);
        if (dto != null) cache(dto);
        return dto;
    }

    public ProductDto getProductById(int id) {
        ProductDto cached = cachedById.get(id);
        if (cached != null) return cached;
        for (ProductDto item : getProducts()) {
            if (item.id == id) return item;
        }
        return null;
    }

    private void cache(ProductDto dto) {
        cachedById.put(dto.id, dto);
        cachedByBarcode.put(dto.barcode, dto);
    }

    private static ProductDto mapProduct(OpenFoodFactsApi.OpenFoodFactsProduct item) {
        if (item == null || isBlank(item.code) || isBlank(item.productName)) return null;
        String code = item.code.trim();
        String name = item.productName.trim();
        String brand = isBlank(item.brands) ? "Бренд не указан" : item.brands.trim();
        String ingredients = isBlank(item.ingredientsText)
                ? "Состав не указан в базе" : item.ingredientsText.trim();
        String imageUrl = isBlank(item.imageFrontUrl) ? "" : item.imageFrontUrl.trim();
        int calories = 0;
        double proteins = -1;
        double fat = -1;
        if (item.nutriments != null) {
            Double kcal = item.nutriments.energyKcal100g != null
                    ? item.nutriments.energyKcal100g : item.nutriments.energyKcal;
            if (kcal != null && kcal >= 0 && kcal < Integer.MAX_VALUE) calories = (int) Math.round(kcal);
            if (item.nutriments.proteins100g != null && item.nutriments.proteins100g >= 0) proteins = item.nutriments.proteins100g;
            if (item.nutriments.fat100g != null && item.nutriments.fat100g >= 0) fat = item.nutriments.fat100g;
        }
        int id = code.hashCode() & 0x7fffffff;
        if (id == 0) id = 1;
        return new ProductDto(id, code, name, brand, imageUrl, ingredients, calories, proteins, fat);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
