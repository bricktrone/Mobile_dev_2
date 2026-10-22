package ru.mirea.khrechko.foodscan;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.khrechko.foodscan.di.ServiceLocator;

/** Builds screen ViewModels using the project's existing ServiceLocator. */
public class FoodScanViewModelFactory implements ViewModelProvider.Factory {
    private final ServiceLocator locator;

    public FoodScanViewModelFactory(Context context) {
        locator = ServiceLocator.get(context.getApplicationContext());
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AuthViewModel.class)) {
            return (T) new AuthViewModel(locator.authRepository());
        }
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(locator.authRepository(), locator.productRepository(),
                    locator.favoriteRepository(), locator.recognitionRepository());
        }
        if (modelClass.isAssignableFrom(ProductDetailViewModel.class)) {
            return (T) new ProductDetailViewModel(locator.authRepository(), locator.productRepository(),
                    locator.favoriteRepository());
        }
        throw new IllegalArgumentException("Неизвестный класс ViewModel: " + modelClass.getName());
    }
}
