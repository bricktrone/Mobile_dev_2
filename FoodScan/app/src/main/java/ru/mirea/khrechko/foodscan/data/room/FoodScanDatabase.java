package ru.mirea.khrechko.foodscan.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavoriteProductEntity.class}, version = 1, exportSchema = false)
public abstract class FoodScanDatabase extends RoomDatabase {
    private static volatile FoodScanDatabase instance;

    public abstract FavoriteProductDao favoriteProductDao();

    public static FoodScanDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (FoodScanDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            FoodScanDatabase.class, "foodscan.db").build();
                }
            }
        }
        return instance;
    }
}
