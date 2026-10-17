package ru.mirea.khrechko.movieproject.data.repository;

import ru.mirea.khrechko.movieproject.data.storage.MovieStorage;
import ru.mirea.khrechko.movieproject.domain.repository.MovieRepository;
import ru.mirea.khrechko.movieproject.domain.models.Movie;

public class MovieRepositoryImpl implements MovieRepository {
    private final MovieStorage movieStorage;

    public MovieRepositoryImpl(MovieStorage movieStorage) {
        this.movieStorage = movieStorage;
    }

    @Override
    public boolean saveMovie(Movie movie) {
        if (movie == null || movie.getName() == null || movie.getName().trim().isEmpty()) {
            return false;
        }
        return movieStorage.save(mapToStorage(movie));
    }

    @Override
    public Movie getMovie() {
        ru.mirea.khrechko.movieproject.data.storage.models.Movie stored = movieStorage.get();
        return stored == null ? null : mapToDomain(stored);
    }

    private ru.mirea.khrechko.movieproject.data.storage.models.Movie mapToStorage(Movie movie) {
        return new ru.mirea.khrechko.movieproject.data.storage.models.Movie(
                movie.getId(), movie.getName(), java.time.LocalDate.now().toString());
    }

    private Movie mapToDomain(ru.mirea.khrechko.movieproject.data.storage.models.Movie movie) {
        return new Movie(movie.getId(), movie.getName());
    }
}
