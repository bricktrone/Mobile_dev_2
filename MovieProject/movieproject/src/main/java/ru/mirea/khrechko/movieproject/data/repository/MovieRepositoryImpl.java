package ru.mirea.khrechko.movieproject.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import ru.mirea.khrechko.movieproject.domain.models.Movie;
import ru.mirea.khrechko.movieproject.domain.repository.MovieRepository;

public class MovieRepositoryImpl implements MovieRepository {

    private static final String PREFS_NAME = "movie_preferences";

    private static final String KEY_MOVIE_ID = "movie_id";
    private static final String KEY_MOVIE_NAME = "movie_name";

    private final SharedPreferences sharedPreferences;

    public MovieRepositoryImpl(Context context) {
        sharedPreferences = context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
        );
    }

    @Override
    public boolean saveMovie(Movie movie) {

        if (movie == null || movie.getName() == null || movie.getName().isEmpty()) {
            return false;
        }

        sharedPreferences.edit()
                .putInt(KEY_MOVIE_ID, movie.getId())
                .putString(KEY_MOVIE_NAME, movie.getName())
                .apply();

        return true;
    }

    @Override
    public Movie getMovie() {

        if (!sharedPreferences.contains(KEY_MOVIE_NAME)) {
            return null;
        }

        int id = sharedPreferences.getInt(KEY_MOVIE_ID, 0);
        String name = sharedPreferences.getString(KEY_MOVIE_NAME, "");

        return new Movie(id, name);
    }
}