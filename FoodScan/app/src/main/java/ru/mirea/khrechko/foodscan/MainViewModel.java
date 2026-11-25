package ru.mirea.khrechko.foodscan;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.khrechko.foodscan.data.network.NetworkException;
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

/** Main screen state. Retrofit catalog data and Room favorites are observed by LiveData. */
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
    private final MutableLiveData<String> catalogError = new MutableLiveData<>();
    private final MediatorLiveData<String> catalogStorageSummary = new MediatorLiveData<>();
    private final MutableLiveData<List<Product>> favoriteDialogItems = new MutableLiveData<>();
    private final MutableLiveData<Product> productFromBarcode = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedProductId = new MutableLiveData<>();
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
        catalogStorageSummary.addSource(catalogError, ignored -> updateCatalogStorageSummary());
        loadInitialData();
    }

    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<List<Product>> getCatalog() { return catalogFromNetwork; }
    public LiveData<String> getCatalogStorageSummary() { return catalogStorageSummary; }
    public LiveData<List<Product>> getFavoriteDialogItems() { return favoriteDialogItems; }
    public LiveData<List<Product>> getFavorites() { return favoritesFromDb; }
    public LiveData<Integer> getSelectedProductId() { return selectedProductId; }
    public LiveData<Product> getProductFromBarcode() { return productFromBarcode; }
    public void clearProductFromBarcode() { productFromBarcode.setValue(null); }
    public LiveData<String> getMessage() { return message; }
    public LiveData<Boolean> getLoggedOut() { return loggedOut; }

    private void loadInitialData() {
        io.execute(() -> {
            try {
                currentUser.postValue(getProfileUseCase.execute());
            } catch (RuntimeException e) {
                Log.e(TAG, "Не удалось получить профиль", e);
            }
            loadCatalogSafely();
            try {
                favoritesFromDb.postValue(getFavoritesUseCase.execute());
            } catch (RuntimeException e) {
                Log.e(TAG, "Не удалось загрузить избранное из Room", e);
                favoritesFromDb.postValue(null);
            }
        });
    }

    /** Retry button calls this; the actual Retrofit request stays on the I/O executor. */
    public void reloadCatalog() {
        catalogFromNetwork.setValue(null);
        catalogError.setValue(null);
        io.execute(this::loadCatalogSafely);
    }

    private void loadCatalogSafely() {
        try {
            List<Product> products = getProductCatalogUseCase.execute();
            catalogFromNetwork.postValue(products);
            catalogError.postValue(null);
        } catch (NetworkException e) {
            Log.e(TAG, "Ошибка загрузки каталога из Open Food Facts", e);
            catalogFromNetwork.postValue(null);
            catalogError.postValue(e.getMessage());
        } catch (RuntimeException e) {
            Log.e(TAG, "Непредвиденная ошибка загрузки каталога", e);
            catalogFromNetwork.postValue(null);
            catalogError.postValue("Не удалось загрузить каталог. Повторите попытку.");
        }
    }

    private void updateCatalogStorageSummary() {
        List<Product> products = catalogFromNetwork.getValue();
        List<Product> favorites = favoritesFromDb.getValue();
        String error = catalogError.getValue();
        String catalogText;
        if (error != null && !error.trim().isEmpty()) {
            catalogText = "Ошибка загрузки каталога: " + error;
        } else {
            catalogText = products == null ? "Каталог Open Food Facts: загрузка…"
                    : "Каталог Open Food Facts: " + products.size() + " продуктов";
        }
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
        io.execute(() -> {
            try { favoritesFromDb.postValue(getFavoritesUseCase.execute()); }
            catch (RuntimeException e) { Log.e(TAG, "Не удалось обновить избранное", e); }
        });
    }

    public void clearFavoriteDialogItems() { favoriteDialogItems.setValue(null); }

    public void selectProduct(int productId) { selectedProductId.setValue(productId); }

    public void loadFavorites() {
        io.execute(() -> {
            try {
                User user = getProfileUseCase.execute();
                currentUser.postValue(user);
                if (user == null || user.isGuest()) {
                    favoritesFromDb.postValue(java.util.Collections.emptyList());
                } else {
                    favoritesFromDb.postValue(getFavoritesUseCase.execute());
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "Не удалось обновить избранное", e);
                favoritesFromDb.postValue(java.util.Collections.emptyList());
            }
        });
    }

    public void refreshProfile() {
        io.execute(() -> {
            try { currentUser.postValue(getProfileUseCase.execute()); }
            catch (RuntimeException e) { Log.e(TAG, "Не удалось обновить профиль", e); }
        });
    }

    public void findProductByBarcode(String barcode) {
        productFromBarcode.setValue(null);
        io.execute(() -> {
            try {
                Product product = findProductByBarcodeUseCase.execute(barcode);
                productFromBarcode.postValue(product);
                if (product == null) message.postValue("Open Food Facts не нашёл продукт с таким штрихкодом");
            } catch (RuntimeException e) {
                message.postValue("Не удалось найти продукт: " + e.getMessage());
            }
        });
    }

    public void runDemoRecognition() { runRecognition("demo-image"); }

    public void runRecognition(String imagePath) {
        io.execute(() -> {
            RecognitionResult result = recognizeProductUseCase.execute(imagePath == null ? "" : imagePath);
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
