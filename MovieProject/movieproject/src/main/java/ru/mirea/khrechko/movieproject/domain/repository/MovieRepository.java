package ru.mirea.khrechko.movieproject.domain.repository;

import ru.mirea.khrechko.movieproject.domain.models.Movie;

public interface MovieRepository {
    public boolean saveMovie(Movie movie);
    public Movie getMovie();
}
