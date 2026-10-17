package ru.mirea.khrechko.foodscan.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteProductEntity product);

    @Query("SELECT * FROM favorite_products WHERE userId = :userId ORDER BY name")
    List<FavoriteProductEntity> getByUserId(String userId);

    @Query("SELECT * FROM favorite_products WHERE userId = :userId AND productId = :productId LIMIT 1")
    FavoriteProductEntity getByUserAndProduct(String userId, int productId);

    @Query("DELETE FROM favorite_products WHERE userId = :userId AND productId = :productId")
    int delete(String userId, int productId);
}
