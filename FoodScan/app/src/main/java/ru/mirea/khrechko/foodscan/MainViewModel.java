package ru.mirea.khrechko.foodscan;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.khrechko.foodscan.domain.FindProductByBarcodeUseCase;
import ru.mirea.khrechko.foodscan.domain.GetFavoritesUseCase;
import ru.mirea.khrechko.foodscan.domain.GetProductCatalogUseCase;
import ru.mirea.khrechko.foodscan.domain.GetProfileUseCase;
import ru.mirea.khrechko.foodscan.domain.LogoutUseCase;
import ru.mirea.khrechko.foodscan.domain.RecognizeProductUseCase;
import ru.mirea.khrechko.foodscan.domain.models.Product;
import ru.mirea.khrechko.foodscan.domain.models.RecognitionResult;
import ru.mirea.khrechko.foodscan.domain.models.User;
import ru.mirea.khrechko.foodscan.domain.repository.AuthRepository;
import ru.mirea.khrechko.foodscan.domain.repository.FavoriteRepository;
import ru.mirea.khrechko.foodscan.domain.repository.ProductRepository;
import ru.mirea.khrechko.foodscan.domain.repository.RecognitionRepository;

/** Main screen state. The network mock and Room data are merged by MediatorLiveData. */
public class MainViewModel extends ViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    private final GetProfileUseCase getProfileUseCase;
    private final LogoutUseCase logoutUseCase;
    private final GetProductCatalogUseCase getProductCatalogUseCase;
    private final FindProductByBarcodeUseCase findProductByBarcodeUseCase;
    private final GetFavoritesUseCase getFavoritesUseCase;
    private final RecognizeProductUseCase recognizeProductUseCase;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> catalogFromNetwork = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> favoritesFromDb = new MutableLiveData<>();
    private final MediatorLiveData<String> catalogStorageSummary = new MediatorLiveData<>();
    private final MutableLiveData<List<Product>> favoriteDialogItems = new MutableLiveData<>();
    private final MutableLiveData<Product> productFromBarcode = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>(false);

    public MainViewModel(AuthRepository authRepository, ProductRepository productRepository,
                         FavoriteRepository favoriteRepository, RecognitionRepository recognitionRepository) {
        Log.d(TAG, "MainViewModel created");
        getProfileUseCase = new GetProfileUseCase(authRepository);
        logoutUseCase = new LogoutUseCase(authRepository);
        getProductCatalogUseCase = new GetProductCatalogUseCase(productRepository);
        findProductByBarcodeUseCase = new FindProductByBarcodeUseCase(productRepository);
        getFavoritesUseCase = new GetFavoritesUseCase(authRepository, favoriteRepository);
        recognizeProductUseCase = new RecognizeProductUseCase(recognitionRepository);

        catalogStorageSummary.addSource(catalogFromNetwork, ignored -> updateCatalogStorageSummary());
        catalogStorageSummary.addSource(favoritesFromDb, ignored -> updateCatalogStorageSummary());
        loadInitialData();
    }

    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<List<Product>> getCatalog() { return catalogFromNetwork; }
    public LiveData<String> getCatalogStorageSummary() { return catalogStorageSummary; }
    public LiveData<List<Product>> getFavoriteDialogItems() { return favoriteDialogItems; }
    public LiveData<Product> getProductFromBarcode() { return productFromBarcode; }
    public LiveData<String> getMessage() { return message; }
    public LiveData<Boolean> getLoggedOut() { return loggedOut; }

    private void loadInitialData() {
        io.execute(() -> {
            currentUser.postValue(getProfileUseCase.execute());
            catalogFromNetwork.postValue(getProductCatalogUseCase.execute());
            favoritesFromDb.postValue(getFavoritesUseCase.execute());
        });
    }

    private void updateCatalogStorageSummary() {
        List<Product> products = catalogFromNetwork.getValue();
        List<Product> favorites = favoritesFromDb.getValue();
        String catalogText = products == null ? "Каталог из NetworkApi: загрузка…"
                : "Каталог из NetworkApi: " + products.size() + " продуктов";
        String favoritesText = favorites == null ? "Избранное из Room: загрузка…"
                : "Избранное из Room: " + favorites.size() + " продуктов";
        catalogStorageSummary.setValue(catalogText + "\n" + favoritesText);
    }

    public void loadFavoritesForDialog() {
        io.execute(() -> {
            User user = getProfileUseCase.execute();
            currentUser.postValue(user);
            if (user == null || user.isGuest()) {
                message.postValue("Избранное доступно после входа в аккаунт");
                return;
            }
            List<Product> favorites = getFavoritesUseCase.execute();
            favoritesFromDb.postValue(favorites);
            favoriteDialogItems.postValue(favorites);
        });
    }

    /** Refreshes the Room source when returning from a product details screen. */
    public void refreshFavoritesSource() {
        io.execute(() -> favoritesFromDb.postValue(getFavoritesUseCase.execute()));
    }

    public void clearFavoriteDialogItems() {
        favoriteDialogItems.setValue(null);
    }

    public void findProductByBarcode(String barcode) {
        productFromBarcode.setValue(null);
        io.execute(() -> {
            Product product = findProductByBarcodeUseCase.execute(barcode);
            productFromBarcode.postValue(product);
            if (product == null) message.postValue("Продукт не найден в тестовом каталоге");
        });
    }

    public void runDemoRecognition() {
        io.execute(() -> {
            RecognitionResult result = recognizeProductUseCase.execute("demo-image");
            if (result == null) {
                message.postValue("Не удалось распознать изображение");
            } else {
                message.postValue("Тестовый результат: " + result.getProductType()
                        + ". Уверенность: " + Math.round(result.getConfidence() * 100)
                        + "%. Сейчас используется заглушка; для настоящего распознавания нужна модель TensorFlow Lite.");
            }
        });
    }

    public void logout() {
        io.execute(() -> {
            logoutUseCase.execute();
            loggedOut.postValue(true);
        });
    }

    public void clearLoggedOut() { loggedOut.setValue(false); }

    @Override
    protected void onCleared() {
        Log.d(TAG, "MainViewModel cleared");
        io.shutdownNow();
        super.onCleared();
    }
}
