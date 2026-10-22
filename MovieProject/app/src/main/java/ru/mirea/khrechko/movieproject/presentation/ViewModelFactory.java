package ru.mirea.khrechko.movieproject.presentation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.khrechko.movieproject.data.repository.MovieRepositoryImpl;
import ru.mirea.khrechko.movieproject.data.storage.MovieStorage;
import ru.mirea.khrechko.movieproject.data.storage.sharedprefs.SharedPrefMovieStorage;
import ru.mirea.khrechko.movieproject.domain.repository.MovieRepository;

/** Creates ViewModels and their dependencies outside the ViewModel itself. */
public class ViewModelFactory implements ViewModelProvider.Factory {
    private final Context applicationContext;

    public ViewModelFactory(Context context) {
        applicationContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            MovieStorage storage = new SharedPrefMovieStorage(applicationContext);
            MovieRepository repository = new MovieRepositoryImpl(storage);
            return (T) new MainViewModel(repository);
        }
        throw new IllegalArgumentException("Неизвестный класс ViewModel: " + modelClass.getName());
    }
}
