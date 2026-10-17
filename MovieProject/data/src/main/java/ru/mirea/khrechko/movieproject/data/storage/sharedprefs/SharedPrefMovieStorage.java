package ru.mirea.khrechko.movieproject.data.storage.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;

import ru.mirea.khrechko.movieproject.data.storage.MovieStorage;
import ru.mirea.khrechko.movieproject.data.storage.models.Movie;

public class SharedPrefMovieStorage implements MovieStorage {
    private static final String PREFS_NAME = "shared_prefs_name";
    private static final String KEY_NAME = "movie_name";
    private static final String KEY_DATE = "movie_date";
    private static final String KEY_ID = "movie_id";

    private final SharedPreferences preferences;

    public SharedPrefMovieStorage(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public Movie get() {
        if (!preferences.contains(KEY_NAME)) return null;
        String name = preferences.getString(KEY_NAME, "");
        String date = preferences.getString(KEY_DATE, "");
        int id = preferences.getInt(KEY_ID, -1);
        return new Movie(id, name, date);
    }

    @Override
    public boolean save(Movie movie) {
        if (movie == null || movie.getName() == null || movie.getName().trim().isEmpty()) {
            return false;
        }
        return preferences.edit()
                .putString(KEY_NAME, movie.getName().trim())
                .putString(KEY_DATE, LocalDate.now().toString())
                .putInt(KEY_ID, movie.getId())
                .commit();
    }
}
