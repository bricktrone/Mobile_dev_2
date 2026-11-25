package ru.mirea.khrechko.foodscan;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.khrechko.foodscan.domain.AddToFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.GetFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.GetProductDetailsUseCase;
import ru.mirea.khrechko.foodscan.domain.GetProfileUseCase;
import ru.mirea.khrechko.foodscan.domain.RemoveFromFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;

/** Details screen logic and favorite toggling. */
public class ProductDetailViewModel extends ViewModel {
    private static final String TAG = ProductDetailViewModel.class.getSimpleName();

    private final GetProductDetailsUseCase getProductDetailsUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final GetFavoritesUseCase getFavoritesUseCase;
    private final AddToFavoritesUseCase addToFavoritesUseCase;
    private final RemoveFromFavoritesUseCase removeFromFavoritesUseCase;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> canSaveFavorite = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private int loadedProductId = -1;

    public ProductDetailViewModel(AuthRepository authRepository, ProductRepository productRepository,
                                  FavoriteRepository favoriteRepository) {
        Log.d(TAG, "ProductDetailViewModel created");
        getProductDetailsUseCase = new GetProductDetailsUseCase(productRepository);
        getProfileUseCase = new GetProfileUseCase(authRepository);
        getFavoritesUseCase = new GetFavoritesUseCase(authRepository, favoriteRepository);
        addToFavoritesUseCase = new AddToFavoritesUseCase(authRepository, favoriteRepository);
        removeFromFavoritesUseCase = new RemoveFromFavoritesUseCase(authRepository, favoriteRepository);
    }

    public LiveData<Product> getProduct() { return product; }
    public LiveData<Boolean> getCanSaveFavorite() { return canSaveFavorite; }
    public LiveData<Boolean> getIsFavorite() { return isFavorite; }
    public LiveData<String> getMessage() { return message; }

    public void loadProduct(int productId) {
        if (loadedProductId == productId && product.getValue() != null) return;
        loadedProductId = productId;
        io.execute(() -> {
            try {
                Product loaded = getProductDetailsUseCase.execute(productId);
                product.postValue(loaded);
                User user = getProfileUseCase.execute();
                boolean allowed = user != null && !user.isGuest();
                canSaveFavorite.postValue(allowed);
                boolean saved = false;
                if (allowed) {
                    List<Product> favorites = getFavoritesUseCase.execute();
                    for (Product item : favorites) if (item.getId() == loaded.getId()) { saved = true; break; }
                }
                isFavorite.postValue(saved);
            } catch (RuntimeException e) {
                Log.e(TAG, "Не удалось загрузить карточку продукта", e);
                product.postValue(null);
                message.postValue("Не удалось загрузить карточку продукта: "
                        + (e.getMessage() == null ? "ошибка сети" : e.getMessage()));
            }
        });
    }

    public void toggleFavorite() {
        Product selected = product.getValue();
        if (selected == null) {
            message.setValue("Карточка продукта ещё загружается");
            return;
        }
        io.execute(() -> {
            User user = getProfileUseCase.execute();
            if (user == null || user.isGuest()) {
                message.postValue("Войдите в аккаунт, чтобы использовать избранное");
                return;
            }
            List<Product> favorites = getFavoritesUseCase.execute();
            boolean exists = false;
            for (Product item : favorites) {
                if (item.getId() == selected.getId()) {
                    exists = true;
                    break;
                }
            }
            boolean changed = exists
                    ? removeFromFavoritesUseCase.execute(selected)
                    : addToFavoritesUseCase.execute(selected);
            if (changed) isFavorite.postValue(!exists);
            message.postValue(changed
                    ? (exists ? "Удалено из избранного" : "Добавлено в избранное")
                    : "Не удалось изменить избранное");
        });
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "ProductDetailViewModel cleared");
        io.shutdownNow();
        super.onCleared();
    }
}
