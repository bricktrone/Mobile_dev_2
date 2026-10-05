package ru.mirea.khrechko.movieproject.data.repository;

import ru.mirea.khrechko.movieproject.domain.Movie;

public class MovieRepository {
    public boolean saveMovie(Movie movie){
        return true;
    }
    public Movie getMovie(){
        return new Movie(1,"Doctor Strange");
    }
}
