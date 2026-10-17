package ru.mirea.khrechko.movieproject.presentation;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.khrechko.movieproject.R;
import ru.mirea.khrechko.movieproject.data.repository.MovieRepositoryImpl;
import ru.mirea.khrechko.movieproject.data.storage.MovieStorage;
import ru.mirea.khrechko.movieproject.data.storage.sharedprefs.SharedPrefMovieStorage;
import ru.mirea.khrechko.movieproject.domain.usecases.GetFavoriteFilmUseCase;
import ru.mirea.khrechko.movieproject.domain.models.Movie;
import ru.mirea.khrechko.movieproject.domain.repository.MovieRepository;
import ru.mirea.khrechko.movieproject.domain.usecases.SaveFilmToFavoriteUseCase;

public class MainActivity extends AppCompatActivity {
    private EditText text;
    private TextView textView;

    private SaveFilmToFavoriteUseCase saveMovieToFavoriteUseCase;
    private GetFavoriteFilmUseCase getFavoriteFilmUseCase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.editTextText);
        textView = findViewById(R.id.textView);

        MovieStorage movieStorage = new SharedPrefMovieStorage(getApplicationContext());
        MovieRepository movieRepository = new MovieRepositoryImpl(movieStorage);

        saveMovieToFavoriteUseCase =
                new SaveFilmToFavoriteUseCase(movieRepository);

        getFavoriteFilmUseCase =
                new GetFavoriteFilmUseCase(movieRepository);

        findViewById(R.id.buttonSave).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Movie movie = new Movie(
                                2,
                                text.getText().toString()
                        );

                        boolean result =
                                saveMovieToFavoriteUseCase.execute(movie);

                        textView.setText(
                                String.format("Save result %s", result)
                        );
                    }
                }
        );

        findViewById(R.id.button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Movie movie =
                                getFavoriteFilmUseCase.execute();

                        if (movie == null) {
                            textView.setText("Favorite movie not found");
                        } else {
                            textView.setText(
                                    String.format(
                                            "Favorite movie: %s",
                                            movie.getName()
                                    )
                            );
                        }
                    }
                }
        );
    }
}