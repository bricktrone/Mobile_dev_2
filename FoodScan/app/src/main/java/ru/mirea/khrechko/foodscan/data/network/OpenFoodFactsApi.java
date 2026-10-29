package ru.mirea.khrechko.foodscan.data.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Path;

/** Read-only Open Food Facts search API. */
public interface OpenFoodFactsApi {
    @Headers({
            "User-Agent: FoodScan/1.0 (Android educational app)",
            "Accept: application/json"
    })
    @GET("api/v2/search")
    Call<SearchResponse> searchProducts(
            @Query("countries_tags_en") String country,
            @Query("page") int page,
            @Query("page_size") int pageSize,
            @Query("fields") String fields
    );

    @Headers({
            "User-Agent: FoodScan/1.0 (Android educational app)",
            "Accept: application/json"
    })
    @GET("api/v2/product/{barcode}")
    Call<ProductResponse> getProductByBarcode(
            @Path("barcode") String barcode,
            @Query("fields") String fields
    );

    class SearchResponse {
        @SerializedName("products")
        public List<OpenFoodFactsProduct> products;
    }

    class ProductResponse {
        @SerializedName("status") public int status;
        @SerializedName("product") public OpenFoodFactsProduct product;
    }

    class OpenFoodFactsProduct {
        @SerializedName("code") public String code;
        @SerializedName("product_name") public String productName;
        @SerializedName("brands") public String brands;
        @SerializedName("image_front_url") public String imageFrontUrl;
        @SerializedName("ingredients_text") public String ingredientsText;
        @SerializedName("nutriments") public Nutriments nutriments;
    }

    class Nutriments {
        @SerializedName("energy-kcal_100g") public Double energyKcal100g;
        @SerializedName("energy-kcal") public Double energyKcal;
    }
}
