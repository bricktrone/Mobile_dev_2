package ru.mirea.khrechko.movieproject.data.storage;

import ru.mirea.khrechko.movieproject.data.storage.models.Movie;

public interface MovieStorage {
    Movie get();
    boolean save(Movie movie);
}
