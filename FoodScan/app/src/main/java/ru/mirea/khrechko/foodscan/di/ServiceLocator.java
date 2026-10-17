package ru.mirea.khrechko.foodscan.di;

import android.content.Context;

import ru.mirea.khrechko.foodscan.data.auth.FirebaseAuthSource;
import ru.mirea.khrechko.foodscan.data.network.NetworkApi;
import ru.mirea.khrechko.foodscan.data.repository.AuthRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.repository.FavoriteRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.repository.ProductRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.repository.RecognitionRepositoryImpl;
import ru.mirea.khrechko.foodscan.data.room.FoodScanDatabase;
import ru.mirea.khrechko.foodscan.data.storage.SharedPrefUserStorage;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;
import ru.mirea.khrechko.foodscan.domain.repository.RecognitionRepository;

public final class ServiceLocator {
    private static volatile ServiceLocator instance;
    private final AuthRepository authRepository;
    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final RecognitionRepository recognitionRepository;

    private ServiceLocator(Context context) {
        Context app = context.getApplicationContext();
        authRepository = new AuthRepositoryImpl(new FirebaseAuthSource(), new SharedPrefUserStorage(app));
        productRepository = new ProductRepositoryImpl(new NetworkApi());
        favoriteRepository = new FavoriteRepositoryImpl(FoodScanDatabase.getInstance(app));
        recognitionRepository = new RecognitionRepositoryImpl(app);
    }

    public static ServiceLocator get(Context context) {
        if (instance == null) {
            synchronized (ServiceLocator.class) {
                if (instance == null) instance = new ServiceLocator(context);
            }
        }
        return instance;
    }

    public AuthRepository authRepository() { return authRepository; }
    public ProductRepository productRepository() { return productRepository; }
    public FavoriteRepository favoriteRepository() { return favoriteRepository; }
    public RecognitionRepository recognitionRepository() { return recognitionRepository; }
}
