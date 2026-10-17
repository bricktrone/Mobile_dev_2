package ru.mirea.khrechko.movieproject.domain.usecases;

import ru.mirea.khrechko.movieproject.domain.models.Movie;
import ru.mirea.khrechko.movieproject.domain.repository.MovieRepository;

public class SaveFilmToFavoriteUseCase {
    private final MovieRepository movieRepository;

    public SaveFilmToFavoriteUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public boolean execute(Movie movie) {
        return movieRepository.saveMovie(movie);
    }
}
