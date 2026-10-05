package ru.mirea.khrechko.movieproject.domain;

public interface MovieRepository {
    public boolean saveMovie(Movie movie);
    public Movie getMovie();
}
