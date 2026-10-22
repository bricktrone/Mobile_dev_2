package ru.mirea.khrechko.movieproject.presentation;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import ru.mirea.khrechko.movieproject.domain.models.Movie;
import ru.mirea.khrechko.movieproject.domain.repository.MovieRepository;
import ru.mirea.khrechko.movieproject.domain.usecases.GetFavoriteFilmUseCase;
import ru.mirea.khrechko.movieproject.domain.usecases.SaveFilmToFavoriteUseCase;

/** Holds screen state and calls domain use cases without depending on Android Views. */
public class MainViewModel extends ViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    private final SaveFilmToFavoriteUseCase saveFilmUseCase;
    private final GetFavoriteFilmUseCase getFavoriteFilmUseCase;
    private final MutableLiveData<String> favoriteMovie = new MutableLiveData<>("Нет данных!");

    public MainViewModel(MovieRepository movieRepository) {
        Log.d(TAG, "MainViewModel created");
        saveFilmUseCase = new SaveFilmToFavoriteUseCase(movieRepository);
        getFavoriteFilmUseCase = new GetFavoriteFilmUseCase(movieRepository);
    }

    public LiveData<String> getFavoriteMovie() {
        return favoriteMovie;
    }

    public void saveMovie(String name) {
        Movie movie = new Movie(2, name == null ? "" : name.trim());
        boolean saved = saveFilmUseCase.execute(movie);
        favoriteMovie.setValue(saved ? "Фильм сохранён" : "Не удалось сохранить фильм. Введите название.");
    }

    public void loadFavoriteMovie() {
        Movie movie = getFavoriteFilmUseCase.execute();
        if (movie == null) {
            favoriteMovie.setValue("Любимый фильм не найден");
        } else {
            favoriteMovie.setValue("Любимый фильм: " + movie.getName());
        }
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "MainViewModel cleared");
        super.onCleared();
    }
}
