package ru.mirea.khrechko.retrofitapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "RetrofitApp";
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private ApiService apiService;
    private TodoAdapter todoAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View errorLayout;
    private TextView textError;
    private Spinner spinnerMode;
    private Button buttonRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewTodos);
        progressBar = findViewById(R.id.progressBar);
        errorLayout = findViewById(R.id.layoutError);
        textError = findViewById(R.id.textViewError);
        spinnerMode = findViewById(R.id.spinnerImageMode);
        buttonRetry = findViewById(R.id.buttonRetry);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new TodoAdapter(this::updateTodo);
        recyclerView.setAdapter(todoAdapter);

        ArrayAdapter<CharSequence> modes = ArrayAdapter.createFromResource(this,
                R.array.image_modes, android.R.layout.simple_spinner_item);
        modes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMode.setAdapter(modes);
        spinnerMode.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                todoAdapter.setImageMode(position);
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        buttonRetry.setOnClickListener(v -> loadTodos());
        loadTodos();
    }

    private void loadTodos() {
        showLoading();
        apiService.getTodos().enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Todo>> call,
                                   @NonNull Response<List<Todo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todoAdapter.setItems(response.body());
                    showList();
                    Log.d(TAG, "Загружено дел: " + response.body().size());
                } else {
                    Log.e(TAG, "Ошибка HTTP: " + response.code());
                    showError("Сервер ответил ошибкой HTTP " + response.code() + ". Повторите запрос.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Todo>> call, @NonNull Throwable t) {
                Log.e(TAG, "Ошибка загрузки списка", t);
                showError("Не удалось загрузить список: " + safeMessage(t) + ". Проверьте интернет.");
            }
        });
    }

    private void updateTodo(Todo todo, boolean completed) {
        boolean previousValue = todo.getCompleted();
        todo.setCompleted(completed);
        apiService.updateTodo(todo.getId(), todo).enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(@NonNull Call<Todo> call, @NonNull Response<Todo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Todo updated = response.body();
                    String state = updated.getCompleted() ? "выполнено" : "не выполнено";
                    Toast.makeText(MainActivity.this,
                            "Дело #" + todo.getId() + " обновлено: " + state,
                            Toast.LENGTH_SHORT).show();
                } else {
                    todo.setCompleted(previousValue);
                    todoAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Ошибка обновления: HTTP " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Todo> call, @NonNull Throwable t) {
                todo.setCompleted(previousValue);
                todoAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Не удалось обновить дело: " + safeMessage(t),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private String safeMessage(Throwable error) {
        String message = error.getLocalizedMessage();
        return message == null || message.trim().isEmpty() ? "неизвестная ошибка" : message;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showList() {
        progressBar.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        textError.setText(message);
        errorLayout.setVisibility(View.VISIBLE);
    }
}
