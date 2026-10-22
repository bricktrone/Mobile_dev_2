package ru.mirea.khrechko.movieproject.presentation;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.khrechko.movieproject.R;

/** The Activity only renders LiveData and forwards user actions to the ViewModel. */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText text;
    private TextView textView;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity created");
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.editTextText);
        textView = findViewById(R.id.textView);

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this))
                .get(MainViewModel.class);
        viewModel.getFavoriteMovie().observe(this, textView::setText);

        findViewById(R.id.buttonSave).setOnClickListener(v ->
                viewModel.saveMovie(text.getText().toString()));
        findViewById(R.id.button).setOnClickListener(v -> viewModel.loadFavoriteMovie());
    }
}
