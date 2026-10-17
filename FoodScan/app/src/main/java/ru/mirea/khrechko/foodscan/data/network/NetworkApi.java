package ru.mirea.khrechko.foodscan.data.network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A deterministic mock of the remote JSON API. The repository depends on this class,
 * not on Android views, so it can later be replaced with a real HTTP client.
 */
public class NetworkApi {
    private static final String MOCK_PRODUCTS_JSON = "["
            + "{\"id\":1,\"barcode\":\"460000000001\",\"name\":\"Молоко\",\"brand\":\"Простоквашино\",\"imageUrl\":\"https://placehold.co/320x220/png?text=Milk\",\"ingredients\":\"Молоко нормализованное\",\"calories\":60},"
            + "{\"id\":2,\"barcode\":\"460000000002\",\"name\":\"Банан\",\"brand\":\"FoodScan\",\"imageUrl\":\"https://placehold.co/320x220/png?text=Banana\",\"ingredients\":\"Банан\",\"calories\":89},"
            + "{\"id\":3,\"barcode\":\"460000000003\",\"name\":\"Яблоко\",\"brand\":\"FoodScan\",\"imageUrl\":\"https://placehold.co/320x220/png?text=Apple\",\"ingredients\":\"Яблоко\",\"calories\":52},"
            + "{\"id\":4,\"barcode\":\"460000000004\",\"name\":\"Шоколад\",\"brand\":\"Alpen Gold\",\"imageUrl\":\"https://placehold.co/320x220/png?text=Chocolate\",\"ingredients\":\"Какао, сахар, сухое молоко\",\"calories\":535},"
            + "{\"id\":5,\"barcode\":\"460000000005\",\"name\":\"Хлеб\",\"brand\":\"Хлебный дом\",\"imageUrl\":\"https://placehold.co/320x220/png?text=Bread\",\"ingredients\":\"Мука пшеничная, вода, соль, дрожжи\",\"calories\":250}"
            + "]";

    public List<ProductDto> getProducts() {
        List<ProductDto> products = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(MOCK_PRODUCTS_JSON);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                products.add(new ProductDto(
                        item.getInt("id"), item.getString("barcode"),
                        item.getString("name"), item.getString("brand"),
                        item.optString("imageUrl", ""), item.optString("ingredients", ""),
                        item.optInt("calories", 0)));
            }
        } catch (JSONException e) {
            throw new IllegalStateException("Не удалось разобрать тестовый JSON каталога", e);
        }
        return products;
    }
}
