package ru.mirea.khrechko.movieproject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ru.mirea.khrechko.movieproject.data.repository.MovieRepositoryImpl;
import ru.mirea.khrechko.movieproject.domain.GetFavoriteFilmUseCase;
import ru.mirea.khrechko.movieproject.domain.Movie;
import ru.mirea.khrechko.movieproject.domain.MovieRepository;
import ru.mirea.khrechko.movieproject.domain.SaveFilmToFavoriteUseCase;

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

        MovieRepository movieRepository =
                new MovieRepositoryImpl(this);

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